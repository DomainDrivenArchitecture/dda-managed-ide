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
    [dda.pallet.dda-managed-vm.domain :as vm-domain]
    [dda.pallet.dda-managed-ide.domain.git :as git]
    [dda.pallet.dda-managed-ide.infra :as infra]))

(def DdaIdeDomainConfig
  (merge
    vm-domain/DdaVmUser
    vm-domain/DdaVmBookmarks
    {:vm-type (s/enum :remote :desktop)
     :dev-platform (s/enum :clojure-atom :clojure-nightlight)}))

(def InfraResult {infra/facility infra/DdaIdeConfig})

;TODO: backup-crate integration

(s/defn ^:always-validate ide-git-config
 [ide-config :- DdaIdeDomainConfig]
 (git/ide-git-config ide-config))

(s/defn ^:always-validate ide-serverspec-config
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
  [ide-config :- DdaIdeDomainConfig]
  {:vm-user (:ide-user ide-config)
   :platform (:vm-platform ide-config)})

(def base-plugins
  ["ink" "minimap" "busy-signal"])

(def clean-typing-plugins
  ["trailing-spaces" "linter" "linter-shellcheck" "linter-write-good" "linter-ui-default" "linter-jsonlint" "linter-spell-html" "minimap-linter"])

(def pair-programming-plugins
  ["atom-pair" "floobits" "motepair"])

(def clojure-plugins
  ["proto-repl" "atom-toolbar" "clojure-plus" "parinfer" "lisp-paredit" "linter-clojure"])

(def git-plugins
  ["git-plus" "tree-view-git-status" "git-time-machine" "language-diff"])

(s/defn atom-config
  "create a atom configuration"
  [vm-platform]
  {:settings (if (= vm-platform :aws)
               #{:install-aws-workaround}
               #{})
   :plugins (into
              []
              (concat base-plugins clean-typing-plugins
                      pair-programming-plugins clojure-plugins
                      git-plugins))})

(s/defn ^:always-validate infra-configuration :- InfraResult
  [domain-config :- DdaIdeDomainConfig]
  (let [{:keys [ide-user vm-platform dev-platform]} domain-config
        user-name (name ide-user)]
    {infra/facility
     (merge
      {:ide-user ide-user}
      (cond
        (= dev-platform :clojure-atom) {:clojure {:os-user-name user-name}
                                        :atom (atom-config vm-platform)}

        (= dev-platform :clojure-nightlight) {:clojure {:os-user-name user-name
                                                        :settings #{:install-nightlight}}}

        :default {}))}))
