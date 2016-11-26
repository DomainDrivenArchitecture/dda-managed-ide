# dda-managed-ide
## configure your aws credentials
Define your credentials in your users home:
~/.pallet/config.clj

```
(defpallet
  :services
    {:aws
      {:account "-your aws account id-",
       :secret "-your unencrypted secret-"}
    }
)
```

If you want to use encrypted credentials instead, you will find described here https://www.domaindrivenarchitecture.org/blog-lang/-/blogs/dda-pallet-uses-gnupg-protected-credenti-34 how to encrypt them.

## start your repl
```
lein repl
```

```
(use 'org.domaindrivenarchitecture.pallet.crate.managed-ide.instantiate-aws-init)

; apply configuration without encryption
(do-sth)
; apply configuration with encryption
(do-sth "-your gnupg key id-" "-your gnupg key passphrase-")
```

## compatability
This crate is working with:
 * clojure 1.7
 * pallet 0.8
 * ubuntu 14.04
 
# License
Published under Apache2.0 License.

Copyright by meissa GmbH 2016
