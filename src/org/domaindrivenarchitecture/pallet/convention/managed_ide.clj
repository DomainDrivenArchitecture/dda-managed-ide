; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns org.domaindrivenarchitecture.pallet.convention.managed-ide
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.config.commons.map-utils :as map-utils]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide :as crate]
    [org.domaindrivenarchitecture.pallet.crate.managed-vm :as vm-crate]
    [org.domaindrivenarchitecture.pallet.crate.backup :as backup-crate]
    [org.domaindrivenarchitecture.pallet.convention.managed-vm :as vm-convention]))

(def ^:dynamic dda-projects
  {:dda-pallet
   ["https://github.com/DomainDrivenArchitecture/dda-config-commons.git"
    "https://github.com/DomainDrivenArchitecture/dda-pallet-commons.git"
    "https://github.com/DomainDrivenArchitecture/dda-pallet.git"
    "https://github.com/DomainDrivenArchitecture/dda-user-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-iptables-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-hardening-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-provider-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-init-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-backup-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-mysql-crate.git"
    "https://github.com/DomainDrivenArchitecture/httpd-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-httpd-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-tomcat-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-liferay-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-linkeddata-crate.git"
    "https://github.com/DomainDrivenArchitecture/dda-managed-vm.git"
    "https://github.com/DomainDrivenArchitecture/dda-managed-ide.git"
    "https://github.com/DomainDrivenArchitecture/dda-pallet-masterbuild.git"]})

(def DdaIdeConventionConfig
  "The convention configuration for managed vms crate."
  {:ide-user s/Keyword
   :vm-platform (s/enum :virtualbox :aws)
   :dev-platform (s/enum :clojure-atom :clojure-nightlight)
   })

(s/defn default-ide-backup-config :- backup-crate/BackupConfig
  "Managed vm crate default configuration"
  [user-key :- s/Keyword]
  (vm-convention/default-vm-backup-config user-key))

(s/defn default-ide-config :- crate/DdaIdeConfig
  "Managed vm crate default configuration"
  [user-key dev-platform vm-platform]
  (map-utils/deep-merge
    {:ide-user user-key
     :project-config dda-projects}
    (cond
      (= dev-platform :clojure-atom) {:clojure {:os-user-name (name user-key)}
                                      :atom {:settings (if (= vm-platform :aws)
                                                         #{:install-aws-workaround}
                                                         #{})
                                             :plugins ["ink" "proto-repl"]}
                                      }
      (= dev-platform :clojure-nightlight) {:clojure {:os-user-name (name user-key)
                                                      :settings #{:install-nightlight}}
                                            }
      :default {})
    ))

(s/defn ide-vm-config :- vm-crate/DdaVmConfig
  [user-key dev-platform vm-platform]
  (cond
    (= dev-platform :clojure-atom) (vm-convention/default-vm-config user-key vm-platform)
    (= dev-platform :clojure-nightlight) {:vm-user user-key
                                          :settings #{:install-open-jdk-8 :install-linus-basics :install-git}})
  )

(s/defn ^:always-validate ide-convention :- {:dda-managed-ide crate/DdaIdeConfig
                                             :dda-managed-vm vm-crate/DdaVmConfig
                                             :dda-backup backup-crate/BackupConfig}
  [convention-config :- DdaIdeConventionConfig]
  (let [user-key (:ide-user convention-config)
        vm-platform (:vm-platform convention-config)
        dev-platform (:dev-platform convention-config)]
    {:dda-managed-ide (default-ide-config user-key dev-platform vm-platform)
     :dda-managed-vm (ide-vm-config user-key dev-platform vm-platform)
     :dda-backup (default-ide-backup-config user-key)}
  ))
