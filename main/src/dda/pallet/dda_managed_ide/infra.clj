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

(ns dda.pallet.dda-managed-ide.infra
  (:require
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.pallet.core.infra :as core-infra]
    [dda.pallet.dda-managed-ide.infra.basics :as basics]
    [dda.pallet.dda-managed-ide.infra.db :as db]
    [dda.pallet.dda-managed-ide.infra.clojure :as clojure]
    [dda.pallet.dda-managed-ide.infra.java :as java]
    [dda.pallet.dda-managed-ide.infra.java-script :as js]
    [dda.pallet.dda-managed-ide.infra.devops :as devops]
    [dda.pallet.dda-managed-ide.infra.python :as py]
    [dda.pallet.dda-managed-ide.infra.atom :as atom]
    [dda.pallet.dda-managed-ide.infra.idea :as idea]
    [dda.pallet.dda-managed-ide.infra.vscode :as vscode]))

(def facility :dda-managed-ide)

(def LeinRepoAuth clojure/RepoAuth)

(def DdaIdeConfig
  {:ide-user s/Keyword
   (s/optional-key :basics) basics/Basics
   (s/optional-key :db) db/Db
   (s/optional-key :clojure) clojure/Clojure
   (s/optional-key :java) java/Java
   (s/optional-key :java-script) js/JavaScript
   (s/optional-key :devops) devops/Devops
   (s/optional-key :atom) atom/Atom
   (s/optional-key :idea) idea/Idea
   (s/optional-key :pycharm) idea/Pycharm
   :ide-settings
   (hash-set (apply s/enum
                    (clojure.set/union
                      basics/Settings
                      db/Settings
                      clojure/Settings
                      devops/Settings
                      js/Settings
                      py/Settings
                      atom/Settings
                      idea/Settings)))})

(s/defn init-system
  [config :- DdaIdeConfig]
  (let [{:keys [ide-settings basics clojure devops java java-script atom]} config
          contains-clojure? (contains? config :clojure)
          contains-devops? (contains? config :devops)
          contains-java? (contains? config :java)
          contains-java-script? (contains? config :java-script)
          contains-basics? (contains? config :basics)
          contains-db? (contains? config :db)
          contains-atom? (contains? config :atom)]
    (pallet.action/with-action-options
      {:sudo-user "root"
       :script-dir "/root/"
       :script-env {:HOME (str "/root")}}
      (js/init-system facility contains-java-script? java-script ide-settings))))

(s/defn install-system
  [config :- DdaIdeConfig]
  (let [{:keys [ide-settings basics db clojure devops java java-script atom idea
                pycharm]} config
          contains-clojure? (contains? config :clojure)
          contains-devops? (contains? config :devops)
          contains-java? (contains? config :java)
          contains-java-script? (contains? config :java-script)
          contains-basics? (contains? config :basics)
          contains-db? (contains? config :db)
          contains-atom? (contains? config :atom)
          contains-idea? (contains? config :idea)
          contains-pycharm? (contains? config :pycharm)]
    (pallet.action/with-action-options
      {:sudo-user "root"
       :script-dir "/root/"
       :script-env {:HOME (str "/root")}})
    (actions/package-manager :update)
    (vscode/testcommand facility)
    (basics/install-system facility ide-settings contains-basics? basics)
    (db/install-system facility ide-settings contains-db? db)
    (clojure/install-system facility contains-clojure? clojure)
    (java/install-system facility contains-java? java)
    ;(js/install-system facility contains-java-script? java-script ide-settings)
    (py/install-system facility ide-settings)
    (devops/install-system facility ide-settings contains-devops? devops)
    (atom/install-system facility ide-settings contains-atom? atom)
    (idea/install-system facility ide-settings contains-idea? contains-pycharm? idea)))

(s/defn install-user
  [config :- DdaIdeConfig]
  (let [{:keys [ide-user java-script ide-settings]} config
        os-user-name (name ide-user)
        contains-java-script? (contains? config :java-script)]
    (pallet.action/with-action-options
      {:sudo-user "root"
       :script-dir (str "/home/" os-user-name "/")
       :script-env {:HOME (str "/home/" os-user-name "/")}}
      (js/install-user facility os-user-name contains-java-script? java-script ide-settings))))

(s/defn configure-system
  [config :- DdaIdeConfig]
  (let [{:keys [ide-user clojure devops atom java]} config
        contains-clojure? (contains? config :clojure)
        contains-devops? (contains? config :devops)
        contains-atom? (contains? config :atom)
        contains-java? (contains? config :java)]
    (pallet.action/with-action-options
      {:sudo-user "root"}
      (java/configure-system facility contains-java? java)
      (devops/configure-system facility (name ide-user) contains-devops? devops))))

(s/defn configure-user
  [config :- DdaIdeConfig]
  (let [{:keys [ide-user clojure devops atom]} config
        os-user-name (name ide-user)
        contains-clojure? (contains? config :clojure)
        contains-devops? (contains? config :devops)
        contains-atom? (contains? config :atom)]
    (pallet.action/with-action-options
      {:sudo-user os-user-name
       :script-dir (str "/home/" os-user-name "/")
       :script-env {:HOME (str "/home/" os-user-name "/")}}
      (clojure/configure-user facility os-user-name contains-clojure? clojure)
      (atom/configure-user facility os-user-name contains-atom? atom)
      (devops/configure-user facility (name ide-user) contains-devops? devops))))

(s/defmethod core-infra/dda-init facility
  [dda-crate config]
  (init-system config))

(s/defmethod core-infra/dda-install facility
  [dda-crate config]
  (install-system config)
  (install-user config))

(s/defmethod core-infra/dda-configure facility
  [dda-crate config]
  (configure-system config)
  (configure-user config))

(s/defmethod core-infra/dda-settings facility
  [dda-crate partial-effective-config])

(def dda-ide-crate
  (core-infra/make-dda-crate-infra
    :facility facility
    :infra-schema DdaIdeConfig))

(def with-dda-ide
  (core-infra/create-infra-plan dda-ide-crate))
