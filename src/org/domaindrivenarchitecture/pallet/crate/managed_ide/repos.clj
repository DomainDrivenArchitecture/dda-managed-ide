; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns  org.domaindrivenarchitecture.pallet.crate.managed-ide.repos
  (:require
    [pallet.actions :as actions]
    [pallet.stevedore :as stevedore]
    [pallet.crate.git :as git]
    ))

(defn add-fingerprint-to-known-hosts
  "add a node qualified by ip or fqdn to the users ~/.ssh/known_hosts file."
  [os-user-name fingerprint]
  (let [command
         (str "echo \"" fingerprint "\" > ~/.ssh/known_hosts")]
     (pallet.action/with-action-options
          {:sudo-user os-user-name
           :script-env {:HOME (str "/home/" os-user-name)}}
          (actions/exec
            {:language :bash}
            (stevedore/script (~command))))))

(defn add-node-to-known-hosts
  "add a node qualified by ip or fqdn to the users ~/.ssh/known_hosts file."
  [os-user-name fqdn-or-ip & {:keys [port]                            ;; optional parameter
       :or {port 22}}]
  (let [home (str "/home/" os-user-name)
        command
         (str "ssh-keyscan -p " port " -H " fqdn-or-ip " >> " home "/.ssh/known_hosts")]
     (pallet.action/with-action-options
          {:sudo-user os-user-name
           :script-env {:HOME (str "/home/" os-user-name)}}
          (actions/exec
            {:language :bash}
            (stevedore/script (~command))))))

(defn clone-repos
  "clone repositories"
  [& {:keys [project-name
             project-parent-path
             custom-repo-name
             os-user-name
             git-user-name
             ssh-repo-urls]
      :or {project-destination "/code/"}}]
  (doseq [url ssh-repo-urls]
    (let [current-repo-path
          (str "/home/" os-user-name project-parent-path project-name "/"
               (if (nil? custom-repo-name)
                 (git/repo-name url)
                 custom-repo-name))]
      ; TODO - review mje 29.08.: move state management completly to pallet/state
      (actions/plan-when-not
        (stevedore/script (directory? current-repo-path))
        ;TODO: check if reachable
        (git/clone
          (if (or
                (.contains url "@")                  ;if username has already been specified ...
                (.contains url "http"))              ;or http is defined ...
            url                                    ; ... assume url to be correct or ...
            (str "ssh://" git-user-name "@" url))  ; ... else add git-user-name
          :checkout-dir current-repo-path))
      )))
