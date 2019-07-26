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


(ns dda.pallet.dda-managed-ide.infra-test
  (:require
   [clojure.test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-managed-ide.infra :as sut]))

(s/set-fn-validation! true)

(def basic-config
  {:ide-user :some-user
   :ide-settings #{}})

(def basic-atom-config
  {:atom
   {:plugins ["proto-repl"]}})

(def wrong-atom-config
  {:atom
   {:plugins [:proto-repl]}})

(deftest test-schema
  (testing
   "test the ide schema"
    (is (s/validate sut/DdaIdeConfig basic-config))
    (is (s/validate sut/DdaIdeConfig (merge basic-config basic-atom-config)))
    (is (thrown? Exception (s/validate sut/DdaIdeConfig (merge basic-config wrong-atom-config))))
    (is (thrown? Exception (s/validate sut/DdaIdeConfig {:unsuported-key :unsupported-value})))))

(deftest plan-def
  (testing
   "test plan-def"
    (is sut/with-dda-ide)))
