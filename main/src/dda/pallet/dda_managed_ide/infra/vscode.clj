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

(ns dda.pallet.dda-managed-ide.infra.vscode
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.config.commons.user-home :as user-env]))

(def Vscode {(s/optional-key :plugins) [{:plugin-name s/Str :plugin-config s/Any}]})

; curl -Lo vscode.deb https://go.microsoft.com/fwlink/?LinkID=760868
; sudo apt install ./vscode.deb


; curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.gpg
; sudo install -o root -g root -m 644 microsoft.gpg /etc/apt/trusted.gpg.d/
; sudo sh -c 'echo "deb [arch=amd64] https://packages.microsoft.com/repos/vscode stable main" > /etc/apt/sources.list.d/vscode.list'
; sudo apt-get install apt-transport-https
; sudo apt-get update
; sudo apt-get install code # or code-insiders
; curl -Lo joker-0.12.2-linux-amd64.zip https://github.com/candid82/joker/releases/download/v0.12.2/joker-0.12.2-linux-amd64.zip
; unzip joker-0.12.2-linux-amd64.zip
; mv joker /usr/local/bin/
; code --install-extension cospaia.clojure4vscode martinklepsch.clojure-joker-linter DavidAnson.vscode-markdownlint

; # Settings can be found at $HOME/.config/Code/User/settings.json

; ; Plugins Jan
; Calva 
; Clojure 
; GitLens
; Python 
; "TODO Highlight"
; Todo Tree