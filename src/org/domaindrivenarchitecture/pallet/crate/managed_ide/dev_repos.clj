; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns  org.domaindrivenarchitecture.pallet.crate.managed-ide.dev-repos
  (:require
    [pallet.actions :as actions]
    [pallet.stevedore :as stevedore]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide.repos :as repos]
    ))

(defn clone-project-repositories
  "clone repositories of one given project"
  [os-user-name git-user-name project-key project-repositories
   & {:keys [project-parent-path]
      :or {project-parent-path "/code/"}}]
  (repos/clone-repos
    :project-name (name project-key)
    :os-user-name os-user-name
    :project-parent-path project-parent-path
    :git-user-name git-user-name
    :ssh-repo-urls project-repositories))

(defn clone-projects
  "clone all projects repositories to ~/code"
  [os-user-name git-user-name
   & {:keys [project-config]}]
  (doseq [[project-key project-repositories] project-config]
    (clone-project-repositories os-user-name git-user-name project-key project-repositories)))

(defn clone-liferay-projects
  "clone all projects repositories to ~/code/liferaysdk"
  [os-user-name git-user-name
   & {:keys [liferay-project-config]
       :or {liferay-project-config {}}}]
  (doseq [[project-key project-repositories] liferay-project-config]
    (clone-project-repositories os-user-name git-user-name project-key project-repositories
                                :project-parent-path "/code/liferaysdk/")))
