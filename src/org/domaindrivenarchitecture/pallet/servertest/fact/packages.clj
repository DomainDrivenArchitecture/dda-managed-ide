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

(ns org.domaindrivenarchitecture.pallet.servertest.fact.packages
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.fact :refer :all]
    [org.domaindrivenarchitecture.pallet.servertest.tests :refer :all]
    [pallet.stevedore :refer :all]
    [pallet.script :as script]
    [pallet.script.lib :refer :all]))

(def res-id-packages ::packages)
(defn define-resources-packages
  "Defines the packages installed."
  []
  (define-resource-from-script res-id-packages "dpkg -l"))