(ns spec-forms.alpha
  (:require [clojure.spec.alpha :as s]
            [spec-tools.core :as st]))

(defmacro validator
  [pred message]
  `(st/spec ~pred {:reason ~message}))
