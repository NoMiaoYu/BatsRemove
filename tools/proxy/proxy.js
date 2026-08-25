// Local reverse proxy for blocked Mojang hosts.
// Streams data to the client incrementally (no read timeout), and if the mirror CDN
// truncates a large download, continues fetching the remainder via Range requests and
// streams it too. Response is sent chunked (no Content-Length) so the client reads until EOF.
const https = require('https');
const fs = require('fs');
const { URL } = require('url');

const UPSTREAM = 'bmclapi.bangbang93.com';
const PORT = 443;
const HOST = '127.0.0.1';
const MAX_REDIRECTS = 8;
const MAX_RANGE = 30;

const tlsOpts = {
  key: fs.readFileSync('key.pem'),
  cert: fs.readFileSync('cert.pem')
};

function openStream(url, headers) {
  return new Promise((resolve, reject) => {
    const u = new URL(url);
    const req = https.request(
      {
        host: u.hostname,
        port: u.port || 443,
        path: u.pathname + u.search,
        method: 'GET',
        headers
      },
      (res) => resolve(res)
    );
    req.on('error', reject);
    req.end();
  });
}

// Follow redirects, return the final response stream + the final URL.
async function openFinalStream(url, headers) {
  let current = url;
  let res = await openStream(current, headers);
  let hops = 0;
  while (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location && hops < MAX_REDIRECTS) {
    res.resume(); // drain redirect body
    current = new URL(res.headers.location, current).toString();
    res = await openStream(current, headers);
    hops++;
  }
  return { res, finalUrl: current };
}

function pipeCount(stream, clientRes) {
  return new Promise((resolve, reject) => {
    let bytes = 0;
    stream.on('data', (c) => (bytes += c.length));
    stream.on('error', reject);
    stream.on('end', () => resolve(bytes));
    stream.pipe(clientRes, { end: false });
  });
}

const server = https.createServer(tlsOpts, async (req, res) => {
  const upstream = `https://${UPSTREAM}${req.url || '/'}`;
  const h = buildHeaders(req);
  try {
    const { res: upRes, finalUrl } = await openFinalStream(upstream, h);
    const expected = parseInt(upRes.headers['content-length'], 10) || 0;
    res.writeHead(upRes.statusCode, {
      'Content-Type': upRes.headers['content-type'] || 'application/octet-stream',
      'Transfer-Encoding': 'chunked'
    });

    let received = 0;
    upRes.on('data', (c) => (received += c.length));
    upRes.pipe(res, { end: false });

    upRes.on('end', async () => {
      let cur = received;
      let guard = 0;
      while (expected > 0 && cur < expected && guard < MAX_RANGE) {
        let rr;
        try {
          rr = await openStream(finalUrl, { ...h, Range: `bytes=${cur}-` });
        } catch (e) {
          break;
        }
        if (rr.statusCode !== 206) {
          rr.resume();
          break;
        }
        cur += await pipeCount(rr, res);
        guard++;
      }
      res.end();
    });

    upRes.on('error', () => {
      try { res.end(); } catch (e) {}
    });
  } catch (err) {
    console.error('[proxy] upstream error', err.message);
    try {
      res.writeHead(502, { 'Content-Type': 'text/plain' });
      res.end('proxy upstream error: ' + err.message);
    } catch (e) {}
  }
});

function buildHeaders(req) {
  const h = {};
  for (const key of Object.keys(req.headers)) {
    const lk = key.toLowerCase();
    if (lk === 'host' || lk === 'connection' || lk === 'accept-encoding' || lk === 'transfer-encoding') {
      continue;
    }
    h[key] = req.headers[key];
  }
  return h;
}

server.listen(PORT, HOST, () => {
  console.log(`[proxy] listening on https://${HOST}:${PORT} -> ${UPSTREAM} (streaming + range-reassembly)`);
});
