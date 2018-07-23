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

(ns dda.pallet.dda-managed-ide.domain.atom
  (:require
    [schema.core :as s]))

(def base-plugins
  ["ink" "minimap" "busy-signal" "atom-toolbar" "atom-meld" "intentions"])

(def clean-typing-plugins
  ["trailing-spaces" "linter" "linter-write-good" "linter-ui-default" "linter-jsonlint"
   "linter-spell" "linter-spell-html" "linter-clojure" "minimap-linter"])

(def pair-programming-plugins
  ["teletype"])

(def clojure-plugins
  ["proto-repl"  "clojure-plus" "parinfer" "lisp-paredit" "linter-clojure"])

(def git-plugins
  ["tree-view-git-status" "git-time-machine" "language-diff" "split-diff"])

(def terraform-plugins
  ["language-terraform" "terraform-fmt"])

(s/defn atom-config
  "create a atom configuration"
  [vm-type contains-clojure? contains-devops?]
  {:settings (if (= vm-type :remote)
               #{:install-aws-workaround}
               #{})
   :plugins (into
              []
              (concat base-plugins clean-typing-plugins
                      pair-programming-plugins git-plugins
                      (when contains-clojure? clojure-plugins)
                      (when contains-devops? terraform-plugins)))})
