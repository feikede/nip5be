# NIP-05 Backend

My private backend for NIP-05 auth.

On events of kind 0 (metadata) you can specify the key "nip05" with
an internet identifier (an email-like address) as the value.
Although there is a link to a very liberal "internet identifier"
specification above, NIP-05 assumes the <local-part> part will be
restricted to the characters a-z0-9-_., case-insensitive.

Upon seeing that, the nostr-client splits the identifier
into <local-part> and <domain> and use these values to
make a GET request to https://<domain>/.well-known/nostr.json?name=<local-part>.

The result is a JSON document object with a key "names" that
then be a mapping of names to hex formatted public keys. If the public
key for the given <name> matches the pubkey from
the metadata event, the client then concludes
that the given pubkey can indeed be referenced by its identifier
