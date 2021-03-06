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
    [dda.pallet.commons.secret :as secret]
    [dda.config.commons.map-utils :as mu]
    [dda.pallet.dda-managed-vm.domain :as vm-domain]
    [dda.pallet.dda-git-crate.domain :as git-domain]
    [dda.pallet.dda-managed-ide.domain.git :as git]
    [dda.pallet.dda-managed-ide.domain.atom :as atom]
    [dda.pallet.dda-managed-ide.domain.idea :as idea]
    [dda.pallet.dda-managed-ide.domain.serverspec :as serverspec]
    [dda.pallet.dda-managed-ide.infra :as infra]))

(def InfraResult {infra/facility infra/DdaIdeConfig})

(def RepoAuth
  {:repo s/Str
   :username secret/Secret
   :password secret/Secret})

(def DdaIdeDomainConfig
  (merge
    vm-domain/DdaVmUser
    vm-domain/DdaVmDomainBookmarks
    vm-domain/DdaVmTargetType
    {:target-type (s/enum :virtualbox :remote-aws :plain)}
    {:ide-platform (hash-set (s/enum :atom :idea :pycharm))
     (s/optional-key :git) git-domain/GitDomain
     (s/optional-key :clojure) {(s/optional-key :lein-auth) [RepoAuth]}
     (s/optional-key :java) {}
     (s/optional-key :java-script) {:nodejs-use s/Str}
     (s/optional-key :bigdata) {}
     (s/optional-key :devops)
     {(s/optional-key :aws)
      {(s/optional-key :simple) {:id secret/Secret
                                 :secret secret/Secret}}
      (s/optional-key :docker) {:bip s/Str}}}))

(def RepoAuthResolved
  (secret/create-resolved-schema RepoAuth))

(def DdaIdeDomainResolvedConfig
  (secret/create-resolved-schema DdaIdeDomainConfig))

;TODO: backup-crate integration

(s/defn ^:always-validate
  ide-git-config
  [ide-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user git]} ide-config
        {:keys [name email git-credentials desktop-wiki credential-store]} user
        ide-git-config (git/ide-git-config name email git-credentials desktop-wiki credential-store)]
    (mu/deep-merge
        {}
        git
        ide-git-config)))

(s/defn ^:always-validate
  ide-serverspec-config
  [domain-config :- DdaIdeDomainResolvedConfig]
  (serverspec/serverspec-prerequisits))

(s/defn ^:always-validate
  dda-vm-domain-configuration
  [ide-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user bookmarks target-type]} ide-config]
    (merge
      {:user user
       :target-type target-type
       :usage-type :desktop-ide}
      (when (contains? ide-config :bookmarks)
        {:bookmarks bookmarks}))))

(s/defn ^:always-validate
  infra-configuration :- InfraResult
  [domain-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user vm-type ide-platform clojure devops java-script]} domain-config
        user-name (:name user)
        contains-clojure? (contains? domain-config :clojure)
        contains-java? (contains? domain-config :java)
        contains-java-script? (contains? domain-config :java-script)
        contains-devops? (contains? domain-config :devops)
        contains-bigdata? (contains? domain-config :bigdata)]
    {infra/facility
     (mu/deep-merge
      {:ide-user (keyword (:name user))
       :ide-settings #{:install-basics
                       :install-tmate
                       :install-asciinema
                       :install-pgtools}
       :basics {:argo-uml {:version "0.34"}
                :yed {:download-url
                      "https://www.yworks.com/resources/yed/demo/yEd-3.20.zip"}}
       :db {:dbvis {:version "10.0.25"}}}
      (when (contains? ide-platform :atom)
        {:atom (atom/atom-config vm-type contains-clojure? contains-devops?)})
      (when (contains? ide-platform :idea)
        {:idea (idea/idea-config vm-type contains-clojure? contains-devops?)
         :ide-settings #{:install-idea-inodes}})
      (when (contains? ide-platform :pycharm)
        {:pycharm (idea/pycharm-config)})
      (when contains-clojure?
         {:clojure clojure
          :java {:java-default-to "/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java"}})
      (when contains-java?
         {:java {:gradle {:version "4.9"}}})
      (if contains-java-script?
         {:java-script java-script
          :ide-settings #{:install-nvm :install-asciinema}}
         {:ide-settings #{:install-npm :install-asciinema}})
      (when contains-devops?
         (mu/deep-merge
           {:devops {:terraform {:version "0.12.11"
                                 :sha256-hash "d61f8758a25bc079bb0833b81f998fbc4cf03bb0f41b995e08204cf5978f700e"}
                     :packer {:version "1.4.4"
                              :sha256-hash "b4dc37877a0fd00fc72ebda98977c2133be9ba6b26bcdd13b1b14a369e508948"}
                     :docker {:bip "192.168.1.1/24"}
                     :aws {}}
            :ide-settings #{:install-pip3
                            :install-pybuilder
                            :install-mfa
                            :install-ami-cleaner}}
           {:devops devops}))
      (when contains-bigdata?
        {:ide-settings #{:install-pip3
                         :install-jupyterlab}}))}))
