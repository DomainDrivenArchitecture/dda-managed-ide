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
    [dda.pallet.dda-managed-ide.infra :as infra]))

(def InfraResult {infra/facility infra/DdaIdeConfig})

(def RepoAuth
  {:repo s/Str
   :username secret/Secret
   :password secret/Secret})

(def DdaIdeDomainConfig
  (merge
    vm-domain/DdaVmUser
    vm-domain/DdaVmBookmarks
    {:target-type (s/enum :virtualbox :remote-aws :plain)}
    {:ide-platform (hash-set (s/enum :atom :idea))
     (s/optional-key :git) git-domain/GitDomainConfig
     (s/optional-key :clojure) {(s/optional-key :lein-auth) [RepoAuth]}
     (s/optional-key :devops)
     {(s/optional-key :aws)
      {(s/optional-key :simple) {:id secret/Secret
                                 :secret secret/Secret}}}}))

(def RepoAuthResolved
  (secret/create-resolved-schema RepoAuth))

(def DdaIdeDomainResolvedConfig
  (secret/create-resolved-schema DdaIdeDomainConfig))

;TODO: backup-crate integration

(s/defn ^:always-validate
  ide-git-config
  [ide-config :- DdaIdeDomainResolvedConfig]
  (git/ide-git-config ide-config))

(s/defn ^:always-validate
  ide-serverspec-config
  [domain-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user ide-platform]} domain-config
        contains-clojure? (contains? domain-config :clojure)
        contains-devops? (contains? domain-config :devops)]
    (mu/deep-merge
      {}
      (when contains-clojure?
        {:file (list
                 {:path (str "/home/" (:name user) "/.lein/profiles.clj")}
                 {:path "/opt/leiningen/lein"}
                 {:path "/etc/profile.d/lein.sh"})})
      (when (contains? ide-platform :atom)
        {:package '({:name "atom"}
                    {:name "python"}
                    {:name "gvfs-bin"})}))))

(s/defn ^:always-validate
  dda-vm-domain-configuration
  [ide-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user bookmarks target-type]} ide-config]
    (merge
      {:user user
       :target-type target-type
       :usage-type :desktop-base}
      (when (contains? ide-config :bookmarks)
        {:bookmarks bookmarks}))))

(s/defn ^:always-validate
  infra-configuration :- InfraResult
  [domain-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user vm-type ide-platform clojure devops]} domain-config
        user-name (:name user)
        contains-clojure? (contains? domain-config :clojure)
        contains-devops? (contains? domain-config :devops)]
    {infra/facility
     (mu/deep-merge
      {:ide-user (keyword (:name user))
       :ide-settings #{:install-idea-inodes
                       :install-basics
                       :install-asciinema}}
      (when (contains? ide-platform :atom)
        {:atom (atom/atom-config vm-type contains-clojure?)})
      (when contains-clojure?
         {:clojure (merge
                     {:os-user-name user-name}
                     clojure)})
      (when contains-devops?
         (mu/deep-merge
           {:devops {:terraform {:version "0.11.7"
                                 :sha256-hash "6b8ce67647a59b2a3f70199c304abca0ddec0e49fd060944c26f666298e23418"}}}
           {:devops devops
            :ide-settings #{:install-mach
                            :install-mfa
                            :install-awscli}})))}))
