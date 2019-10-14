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
   [data-test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-managed-ide.domain :as sut]
   [dda.pallet.dda-managed-ide.domain.serverspec :as serverspec]))

(s/set-fn-validation! true)

(deftest should-throw-exception-on-invalid-input
  (is (thrown? Exception (sut/ide-git-config {})))
  (is (thrown? Exception (sut/ide-serverspec-config {})))
  (is (thrown? Exception (sut/dda-vm-domain-configuration {})))
  (is (thrown? Exception (sut/infra-configuration {}))))

(defdatatest should-generate-git-domain-config [input expected]
  (is (= expected
         (sut/ide-git-config input))))

(defdatatest should-generate-serverspec-domain-config [input expected]
  (is (= expected
         (serverspec/serverspec-prerequisits))))

(defdatatest should-generate-vm-domain-config [input expected]
  (is (= expected
         (sut/dda-vm-domain-configuration input))))

(defdatatest should-generate-infra-config [input expected]
  (is (= expected
         (sut/infra-configuration input))))
