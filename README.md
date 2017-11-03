# dda-managed-ide
[![Clojars Project](https://img.shields.io/clojars/v/dda/dda-managed-ide.svg)](https://clojars.org/dda/dda-managed-ide)
[![Build Status](https://travis-ci.org/DomainDrivenArchitecture/dda-managed-ide.svg?branch=master)](https://travis-ci.org/DomainDrivenArchitecture/dda-managed-ide)

[![Slack](https://img.shields.io/badge/chat-clojurians-green.svg?style=flat)](https://clojurians.slack.com/messages/#dda-pallet/) | [<img src="https://domaindrivenarchitecture.org/img/meetup.svg" width=50 alt="DevOps Hacking with Clojure Meetup"> DevOps Hacking with Clojure](https://www.meetup.com/de-DE/preview/dda-pallet-DevOps-Hacking-with-Clojure) | [Website & Blog](https://domaindrivenarchitecture.org)


dda-pallet is a DevOps System combining the great framework pallet from Hugo Duncan and principles of Domain Driven Design (overview can be found here: https://domaindrivenarchitecture.org/pages/dda-pallet/ ).

The Module dda-managed-ide is able to automate the setup of your personal ide in a repeatable manner.

## configure your aws credentials
Define your credentials in your users home:
~/.pallet/config.clj

```clojure
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

## start included nightlight ide
```
lein nightlight --port 8080
```

## compatability
This crate is working with:
 * clojure 1.7
 * pallet 0.8
 * ubuntu 14.04

# License
Published under Apache2.0 License.

Copyright by meissa GmbH 2016
