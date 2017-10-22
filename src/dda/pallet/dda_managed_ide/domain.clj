; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns dda.pallet.dda-managed-ide.domain
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

(s/defn ^:always-validate ide-git-config :- git/GitDomainConfig
 [ide-config :- DdaIdeDomainConfig]
 (let [{:keys [ide-user user-email] :or {user-email (str (name ide-user) "@domain")}} ide-config]
   {:os-user ide-user
    :user-email user-email
    :repos dda-projects}))

(s/defn ^:always-validate ide-serverspec-config :- serverspec/ServerTestDomainConfig
 [ide-config :- DdaIdeDomainConfig]
 (let [{:keys [dev-platform]} ide-config
       file-config '({:path "/opt/leiningen/lein"}
                     {:path "/etc/profile.d/lein.sh"}
                     ;TODO: needs to be tested
                     {:path "~/.lein/profiles.clj"})
       platform-dep-config (if (and (= vm-platform :aws) (= dev-platform :clojure-atom))
                             (concat file-config '({:path "/usr/share/atom/libxcb.so.1"})')
                             file-config)]
   (map-utils/deep-merge
    {:file platform-dep-config}
    (cond
      (= dev-platform :clojure-atom) {:package '({:name "atom"}
                                                 {:name "python"}
                                                 {:name "gvfs-bin"})}

      :default {}))))

(s/defn ^:always-validate dda-vm-domain-configuration
  [domain-config :- DomainConfig]
  {:vm-user (:ide-user domain-config)
   :platform (:vm-platform domain-config)})

(s/defn ^:always-validate infra-configuration :- InfraResult
  [domain-config :- DdaIdeDomainConfig]
  (let [{:keys [ide-user vm-platform dev-platform]} domain-config
        user-name (name ide-user)]
    {infra/facility
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

        :default {}))}))
