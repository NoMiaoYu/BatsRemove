// Generates a self-signed cert (with SAN for both blocked Mojang hosts)
// used by the local MITM reverse proxy, plus a DER copy for Java truststore import.
const forge = require('node-forge');
const fs = require('fs');

const hosts = [
  'piston-meta.mojang.com',
  'piston-data.mojang.com',
  'launchermeta.mojang.com',
  'launcher.mojang.com'
];
const keys = forge.pki.rsa.generateKeyPair(2048);

const cert = forge.pki.createCertificate();
cert.publicKey = keys.publicKey;
cert.serialNumber = '01' + Date.now().toString(16);
cert.validity.notBefore = new Date(Date.now() - 60 * 60 * 1000);
cert.validity.notAfter = new Date(Date.now() + 3650 * 24 * 60 * 60 * 1000);

const attrs = [{ name: 'commonName', value: 'piston-meta.mojang.com' }];
cert.setSubject(attrs);
cert.setIssuer(attrs);

const ext = [{ name: 'basicConstraints', cA: true }];
ext.push({ name: 'keyUsage', keyCertSign: true, digitalSignature: true, keyEncipherment: true });
ext.push({
  name: 'subjectAltName',
  altNames: hosts.map((h) => ({ type: 2, value: h }))
});
cert.setExtensions(ext);

cert.sign(keys.privateKey, forge.md.sha256.create());

const certPem = forge.pki.certificateToPem(cert);
const keyPem = forge.pki.privateKeyToPem(keys.privateKey);
const der = forge.asn1.toDer(forge.pki.certificateToAsn1(cert)).getBytes();

fs.writeFileSync('cert.pem', certPem);
fs.writeFileSync('key.pem', keyPem);
fs.writeFileSync('cert.cer', Buffer.from(der, 'binary'));

console.log('Generated cert.pem, key.pem, cert.cer');
console.log('SAN: ' + hosts.join(', '));
