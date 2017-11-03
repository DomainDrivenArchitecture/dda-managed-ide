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


(ns dda.pallet.domain.managed-ide
  (:require
    [schema.core :as s]
    [dda.config.commons.map-utils :as map-utils]
    [dda.pallet.crate.managed-ide :as crate]
    [dda.pallet.dda-managed-vm.infra :as vm-crate]
    [org.domaindrivenarchitecture.pallet.crate.backup :as backup-crate]
    [dda.pallet.domain.managed-ide.atom :as domain-atom]
    [dda.pallet.domain.managed-ide.repos :as domain-repos]
    [dda.pallet.dda-managed-vm.domain :as vm-convention]))

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
     :project-config domain-repos/dda-projects}
    (cond
      (= dev-platform :clojure-atom) {:clojure {:os-user-name (name user-key)}
                                      :atom (domain-atom/atom-config vm-platform)}
      (= dev-platform :clojure-nightlight) {:clojure {:os-user-name (name user-key)
                                                      :settings #{:install-nightlight}}}
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
