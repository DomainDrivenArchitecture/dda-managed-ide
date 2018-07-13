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

(ns dda.pallet.dda-managed-ide.domain-2-0
  {:deprecated "2.0"}
  (:require
    [schema.core :as s]
    [dda.pallet.commons.secret :as secret]
    [dda.config.commons.map-utils :as mu]
    [dda.pallet.dda-managed-vm.domain :as vm-domain]
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
    vm-domain/DdaVmDomainConfig
    {:dev-platform (hash-set (s/enum :clojure :devops))
     :ide-platform (hash-set (s/enum :atom))
     :user {:project-set (hash-set (s/enum :dda-pallet))
            (s/optional-key :lein-auth) [RepoAuth]}}))

(def RepoAuthResolved
  (secret/create-resolved-schema RepoAuth))

(def DdaIdeDomainResolvedConfig
  (secret/create-resolved-schema DdaIdeDomainConfig))

;TODO: backup-crate integration

(s/defn ^:always-validate ide-git-config
 [ide-config :- DdaIdeDomainResolvedConfig]
 (git/ide-git-config ide-config))

(s/defn ^:always-validate
  ide-serverspec-config
  [ide-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user dev-platform vm-platform]} ide-config
        profile-map (assoc {} :path (str "/home/" (:name user) "/.lein/profiles.clj"))
        profile-list (conj '() profile-map)
        file-config (concat '({:path "/opt/leiningen/lein"}
                              {:path "/etc/profile.d/lein.sh"})
                     profile-list)
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

(s/defn ^:always-validate
  dda-vm-domain-configuration
  [ide-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user bookmarks vm-type]} ide-config]
    (merge
      {:user user}
      (when (contains? ide-config :bookmarks)
        {:bookmarks bookmarks})
      (when = vm-type :remote
        {:target-type :remote-aws
         :usage-type :desktop-base})
      (when = vm-type :desktop-office
        {:target-type :virtualbox
         :usage-type :desktop-office})
      (when = vm-type :desktop-novbox
        {:target-type :plain
         :usage-type :desktop-base}))))

(s/defn ^:always-validate
  infra-configuration :- InfraResult
  [domain-config :- DdaIdeDomainResolvedConfig]
  (let [{:keys [user vm-type dev-platform lein-auth]} domain-config
        user-name (:name user)]
    {infra/facility
     (merge
      {:ide-user (keyword (:name user))}
      (cond
        (= dev-platform :clojure-atom)
        {:atom (atom/atom-config vm-type)
         :clojure (merge
                   {:os-user-name user-name}
                   (when (contains? domain-config :lein-auth)
                     {:lein-auth lein-auth}))}
        (= dev-platform :clojure-nightlight)
        {:clojure (merge
                    {:os-user-name user-name
                     :settings #{:install-nightlight}}
                    (when (contains? domain-config :lein-auth)
                      {:lein-auth lein-auth}))}
        :default {}))}))
