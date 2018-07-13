# Infra API
The Infra configuration is a configuration on the infrastructure level of a crate. It contains the complete configuration options that are possible with the crate functions. You can find the details of the infra configurations at the other crates used:
* [dda-user-crate](https://github.com/DomainDrivenArchitecture/dda-user-crate)
* [dda-git-crate](https://github.com/DomainDrivenArchitecture/dda-git-crate)
* [dda-serverspec-crate](https://github.com/DomainDrivenArchitecture/dda-serverspec-crate)

For installation & configuration with the dda-managed-ide the schema is:
```clojure
(def DdaIdeConfig
  {(optional-key :clojure) {:os-user-name Str,
                            (optional-key :signing-gpg-key) Str,
                            (optional-key :lein-auth) [{:password Str,
                                                        :username Str,
                                                        :repo Str}],
                            (optional-key :settings) #{(enum
                                                        :install-nightlight)}},
   (optional-key :atom) {(optional-key :plugins) [Str],
                         :settings #{(enum :install-aws-workaround)}},
   :ide-settings #{(enum :install-idea-inodes
                         :install-basics
                         :install-mfa
                         :install-asciinema)}
   :ide-user Keyword}
```

## License
Published under [apache2.0 license](LICENSE.md)
