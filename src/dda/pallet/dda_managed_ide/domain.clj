; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns dda.pallet.domain.managed-ide
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.config.commons.map-utils :as map-utils]
    [dda.pallet.crate.managed-ide :as crate]
    [dda.pallet.crate.managed-vm :as vm-crate]
    [org.domaindrivenarchitecture.pallet.crate.backup :as backup-crate]
    [dda.pallet.domain.managed-vm :as vm-convention]))

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

(def DdaIdeDomainConfig
  {:ide-user s/Keyword
   :vm-platform (s/enum :virtualbox :aws)
   :dev-platform (s/enum :clojure-atom :clojure-nightlight)})


;TODO: gitconfig (dda-projects), serverspec config

(s/defn ^:always-validate dda-vm-domain-configuration
  [domain-config :- DomainConfig]
  {:vm-user (:ide-user domain-config)
   :platform (:vm-platform domain-config)})

;TODO: brauchen wir Backup-Crate überhaupt noch, wenn er doch im
;VM schon eingerichtet und verwendet wird?
;Mehr als home wollen wir gar nicht sichern

; (s/defn default-ide-backup-config :- backup-crate/BackupConfig
;   [user-key :- s/Keyword]
;   (vm-convention/default-vm-backup-config user-key))

(s/defn ^:always-validate infra-configuration :- InfraResult
  [domain-config :- DomainConfig]
  (let [{:keys [ide-user vm-platform dev-platform]} domain-config
        user-name (name ide-user)]
    (map-utils/deep-merge
      {:ide-user ide-user}
      (cond
        (= dev-platform :clojure-atom) {:clojure {:os-user-name user-name}
                                        :atom {:settings (if (= vm-platform :aws)
                                                           #{:install-aws-workaround}
                                                           #{})
                                               :plugins ["ink" "proto-repl"]}}

        (= dev-platform :clojure-nightlight) {:clojure {:os-user-name user-name
                                                        :settings #{:install-nightlight}}}

        :default {}))))
