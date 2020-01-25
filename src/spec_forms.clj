(ns spec-forms
  (:require [clojure.alpha.spec :as s]
            [spec-forms.impl]))

(defn validator [f message-or-fn]
  (s/resolve-spec
   (list `validator f
         (if (fn? message-or-fn) message-or-fn (constantly message-or-fn)))))
