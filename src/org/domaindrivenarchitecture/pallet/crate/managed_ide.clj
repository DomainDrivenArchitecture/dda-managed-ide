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
(ns org.domaindrivenarchitecture.pallet.crate.managed-ide
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.api :as api]
    [pallet.actions :as actions]
    [pallet.crate :as crate]
    [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide.clojure :as clojure]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide.atom :as atom]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide.dev-repos :as dev-repos]
    [org.domaindrivenarchitecture.pallet.servertest.fact.packages :as package-fact]
    [org.domaindrivenarchitecture.pallet.servertest.test.packages :as package-test]))

(def facility :dda-managed-ide)
(def version  [0 1 0
               ])

(def GitProjectConfig
"Configuration of projects clone location"
{s/Keyword [s/Str]})

(def DdaIdeConfig
  "The configuration for managed ide crate."
  {:project-config GitProjectConfig
   :ide-user s/Keyword
   (s/optional-key :clojure) clojure/LeiningenUserProfileConfig
   (s/optional-key :settings) (hash-set (s/enum :install-atom))}
  )

(s/defn install-system
  "install common used packages for ide"
  [config :- DdaIdeConfig]
  (let [settings (-> config :settings)]
    (pallet.action/with-action-options
      {:sudo-user "root"
       :script-dir "/root/"
       :script-env {:HOME (str "/root")}}
      (when (contains? config :clojure)
        (clojure/install-leiningen))
      (when (contains? settings :install-atom)
        (atom/install))
      )))

(s/defn install-user
  "install common used packages for ide"
  [config :- DdaIdeConfig]
  (let [os-user-name (name (-> config :ide-user))
  git-user-name (:git-user-name config)
  project-config (:project-config config)]
    (pallet.action/with-action-options
      {:sudo-user os-user-name
       :script-dir (str "/home/" os-user-name "/")
       :script-env {:HOME (str "/home/" os-user-name "/")}}
      (when (contains? config :clojure)
        (clojure/configure-user-leiningen (-> config :clojure)))
      (when (contains? config :project-config)
      (dev-repos/clone-projects os-user-name git-user-name :project-config project-config)
    ))))

(s/defmethod dda-crate/dda-install facility
  [dda-crate config]
  "dda managed vm: install routine"
  (println config)
  (install-system config)
  (install-user config)
  )

(s/defmethod dda-crate/dda-settings facility
  [dda-crate partial-effective-config]
  ;(package-fact/collect-packages-fact)
  )

(s/defmethod dda-crate/dda-test facility
  [dda-crate partial-effective-config]
  (package-test/test-installed? "xxxx")
  )

(def dda-ide-crate
  (dda-crate/make-dda-crate
    :facility facility
    :version version))

(def with-dda-ide
  (dda-crate/create-server-spec dda-ide-crate))
