; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(ns dda.pallet.dda-managed-ide.domain
  (:require
    [schema.core :as s]
    [dda.pallet.dda-git-crate.domain :as git]
    [dda.pallet.dda-serverspec-crate.domain :as serverspec]
    [dda.pallet.dda-managed-vm.domain :as vm-crate]
    [dda.pallet.dda-manged-ide.infra :as infra]))

(def DdaIdeDomainConfig
  {:ide-user s/Keyword
   :vm-platform (s/enum :virtualbox :aws)
   :dev-platform (s/enum :clojure-atom :clojure-nightlight)})

(def InfraResult {infra/facility infra/DdaIdeConfig})

;TODO: backup-crate integration

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

(s/defn ^:always-validate ide-git-config :- git/GitDomainConfig
 [ide-config :- DdaIdeDomainConfig]
 (let [{:keys [ide-user user-email] :or {user-email (str (name ide-user) "@domain")}} ide-config]
   {:os-user ide-user
    :user-email user-email
    :repos dda-projects}))

(s/defn ^:always-validate ide-serverspec-config :- serverspec/ServerTestDomainConfig
 [ide-config :- DdaIdeDomainConfig]
 (let [{:keys [dev-platform vm-platform]} ide-config
       file-config '({:path "/opt/leiningen/lein"}
                     {:path "/etc/profile.d/lein.sh"}
                     {:path "~/.lein/profiles.clj"})
       platform-dep-config (if (and (= vm-platform :aws) (= dev-platform :clojure-atom))
                             (concat file-config '({:path "/usr/share/atom/libxcb.so.1"}))
                             file-config)]
   (merge
    {:file platform-dep-config}
    (cond
      (= dev-platform :clojure-atom) {:package '({:name "atom"}
                                                 {:name "python"}
                                                 {:name "gvfs-bin"})}

      :default {}))))

(s/defn ^:always-validate dda-vm-domain-configuration
  [domain-config :- vm-crate/DdaVmDomainConfig]
  {:vm-user (:ide-user domain-config)
   :platform (:vm-platform domain-config)})

(s/defn ^:always-validate infra-configuration :- InfraResult
  [domain-config :- DdaIdeDomainConfig]
  (let [{:keys [ide-user vm-platform dev-platform]} domain-config
        user-name (name ide-user)]
    {infra/facility
     (merge
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
