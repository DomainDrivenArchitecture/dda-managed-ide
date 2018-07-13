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


(ns dda.pallet.dda-managed-ide.domain-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.dda-managed-ide.domain :as sut]))

(def config-1
  {:user {:name  "test"
          :password "pwd"}
   :vm-type :remote
   :dev-platform :clojure-atom})

(def config-2
  {:user {:name  "test"
          :password "pwd"}
   :vm-type :desktop
   :dev-platform :clojure-atom})

(def config-3
  {:vm-type :desktop
   :bookmarks [{:name "Bookmarks Toolbar"
                :links [["url" "name"]]}]
   :user {:name  "test"
          :password "pwd"}
   :dev-platform :clojure-atom})



(deftest test-git-config
  (testing
    "test the git config creation"
    (is (thrown? Exception (sut/ide-git-config {})))
    (is (sut/ide-git-config config-1))
    (is (sut/ide-git-config config-2))))

(deftest test-serverspec-config
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/ide-serverspec-config {})))
    (is (sut/ide-serverspec-config config-1))
    (is (sut/ide-serverspec-config config-2))))

(deftest test-infra-configuration
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/infra-configuration {})))
    (is (sut/infra-configuration config-1))
    (is (sut/infra-configuration config-3))))
