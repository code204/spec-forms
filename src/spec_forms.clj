(ns spec-forms
  (:require [clojure.alpha.spec :as s]
            [spec-forms.impl]))

(defn validator [f message-fn]
  (s/resolve-spec
   (list `validator f message-fn)))
