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

(ns dda.pallet.dda-managed-ide.infra.idea
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.config.commons.user-home :as user-env]))

(def Idea {(s/optional-key :plugins) [{:plugin-name s/Str :plugin-config s/Any}]})

(def Pycharm {(s/optional-key :plugins) [{:plugin-name s/Str :plugin-config s/Any}]})

(defmulti install-idea-plugin
          (fn [plugin-config] (:plugin-name plugin-config)))

(defmethod install-idea-plugin "cursive"
  []
  (actions/as-action
    (logging/info (str  "-configure system: install idea cursive plugin"))))

(defn install-umake
  []
  ;install umake to make for easier installation for idea
  (actions/packages :aptitude ["ubuntu-make"]))

(defn install-idea
  ; Installs the Intellij Idea community edition IDE through umake.
  ; Install location is ~/.<Product><Version> e.g. ~/.IdeaIC2018.1
  [facility]
  (actions/as-action
    (logging/info (str facility "-configure system: install idea community edition")))
  (actions/exec-checked-script
      "install idea community edition"
      ("umake" "ide" "idea" "~/.idea/")))

(defn install-pycharm
  ; Installs the Intellij Idea community edition IDE through umake.
  ; Install location is ~/.<Product><Version> e.g. ~/.IdeaIC2018.1
  [facility]
  (actions/as-action
    (logging/info (str facility "-configure system: install pycharm community edition")))
  (actions/exec-checked-script
    "install pycharm community edition"
    ("umake" "ide" "pycharm" "~/.pycharm/")))

(def Settings
   #{:install-idea-inodes})

(defn install-idea-inodes
  [facility]
  (actions/as-action
    (logging/info (str facility "-configure system: install-idea-inodes")))
  (actions/exec-checked-script
    "adjust inodes for idea"
    ("echo" "\"fs.inotify.max_user_watches = 524288\"" ">" "/etc/sysctl.conf")
    ("sysctl" "-p")))

(s/defn install-system
  [facility ide-settings contains-idea? contains-pycharm? idea-config]
  (when (contains? ide-settings :install-idea-inodes)
     (install-idea-inodes facility))
  (install-umake)
  (when contains-idea?
    (install-idea facility))
  (when contains-pycharm?
    (install-pycharm facility)))
