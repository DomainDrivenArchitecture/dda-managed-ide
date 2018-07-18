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
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.pallet.core.infra :as core-infra]
    [dda.pallet.dda-managed-ide.infra.basics :as basics]
    [dda.pallet.dda-managed-ide.infra.clojure :as clojure]
    [dda.pallet.dda-managed-ide.infra.devops :as devops]
    [dda.pallet.dda-managed-ide.infra.atom :as atom]
    [dda.pallet.dda-managed-ide.infra.idea :as idea]))

(def facility :dda-managed-ide)

(def LeinRepoAuth clojure/RepoAuth)

(def DdaIdeConfig
  {:ide-user s/Keyword
   (s/optional-key :clojure) clojure/LeiningenUserProfileConfig
   (s/optional-key :devops) {:terraform devops/Terraform
                             (s/optional-key :aws) devops/AwsCredentials}
   (s/optional-key :atom) {:settings (hash-set (s/enum :install-aws-workaround))
                           (s/optional-key :plugins) [s/Str]}
   :ide-settings
   (hash-set (apply s/enum
                    (clojure.set/union
                      basics/Settings
                      idea/Settings
                      clojure/Settings
                      devops/Settings)))})

(s/defn install-system
  [config :- DdaIdeConfig]
  (let [{:keys [ide-settings devops]} config]
    (pallet.action/with-action-options
      {:sudo-user "root"
       :script-dir "/root/"
       :script-env {:HOME (str "/root")}}
      (basics/install-system facility ide-settings)
      (idea/install-system facility ide-settings)
      (clojure/install-system facility config)
      (devops/install-system facility ide-settings (:terraform devops))
      (when (contains? config :atom)
        (actions/as-action
            (logging/info (str facility "-install system: atom")))
        (atom/install config)))))

(s/defn configure-user
  [config :- DdaIdeConfig]
  (let [{:keys [ide-user clojure devops]} config
        os-user-name (name ide-user)
        contains-clojure? (contains? config :clojure)
        contains-devops? (contains? config :devops)]
    (pallet.action/with-action-options
      {:sudo-user os-user-name
       :script-dir (str "/home/" os-user-name "/")
       :script-env {:HOME (str "/home/" os-user-name "/")}}
      (clojure/configure-user facility contains-clojure? os-user-name clojure)
      (devops/configure-user facility contains-devops? os-user-name (:aws devops))
      (when (contains? config :atom)
        (actions/as-action
          (logging/info (str facility "-configure user: atom")))
        (atom/install-user-plugins config)))))


(s/defmethod core-infra/dda-install facility
  [dda-crate config]
  (install-system config))

(s/defmethod core-infra/dda-configure facility
  [dda-crate config]
  (configure-user config))

(s/defmethod core-infra/dda-settings facility
  [dda-crate partial-effective-config])

(def dda-ide-crate
  (core-infra/make-dda-crate-infra
    :facility facility
    :infra-schema DdaIdeConfig))

(def with-dda-ide
  (core-infra/create-infra-plan dda-ide-crate))
