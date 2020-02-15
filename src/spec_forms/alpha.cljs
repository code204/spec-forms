(ns spec-forms.alpha
  (:require [clojure.spec.alpha :as s]
            [spec-tools.core :as st])
  (:require-macros [spec-forms.alpha]))

(defn contains-key-pred? [[op args [con? arg & ks]]]
  (and (= '[cljs.core/fn [%] cljs.core/contains? %]
         [op args con? arg])
    (= 1 (count ks))))

(defn spec-problem->message [prob opts]
  (or (:reason prob)
    (:default-failure-message opts)
    "Failed validation"))

(defn spec-problem->reform-error [{:keys [pred reason via] :as prob}
                                  & [{:keys [message-fn
                                             required-key-message]}
                                     :as opts]]
  (let [rest-via (vec (rest via))]
    (if (and (empty? rest-via) (contains-key-pred? pred))
      (let [[_ _ [_ _ k]] pred]
        {:korks #{[k]}
         :error-message
         (cond
           (fn? required-key-message) (required-key-message k)
           (nil? required-key-message) "Required"
           :else required-key-message)})
      {:korks (hash-set rest-via)
       :error-message ((or message-fn spec-problem->message) prob opts)})))

(defn validate! [data ui-state spec & [opts]]
  (let [d (or @data {})
        ed (s/explain-data spec d)]
    (if-not ed
      (do (swap! ui-state assoc :validation-errors nil)
        true)
      (do (->> ed :cljs.spec.alpha/problems
            (mapv #(spec-problem->reform-error % opts))
            (swap! ui-state assoc :validation-errors))
          false))))
