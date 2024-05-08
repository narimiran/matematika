(ns matematika
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]))


(def limit 10)

(def initial-state
  {:a (rand-int limit)
   :b (rand-int limit)
   :c ""
   :tocnih 0
   :uzastopnih 0
   :naslov "Dobrodošli na trening matematike!"
   :text ""
   :odgovori []})

(defn event-value [e]
  #_(-> e .-target .-value)
  (.. e -target -value))

(defonce state
  (r/atom initial-state))





(defn tocan-odgovor []
  (let [{:keys [a b c]} @state]
    (swap! state update :odgovori conj [a b c])
    (swap! state assoc
           :text (str "Bravo! " a " + " b " = " c)
           :a (rand-int limit)
           :b (rand-int limit)
           :c "")
    (swap! state update :tocnih inc)
    (swap! state update :uzastopnih inc)
    ))

(defn netocan-odgovor []
  (swap! state assoc :text (str "Neće biti, pokušaj ponovno..."))
  (swap! state assoc :uzastopnih 0)
  (swap! state assoc :odgovori []))


(defn check-math []
  (let [{:keys [a b c]} @state]
    (if (= c (+ a b))
      (tocan-odgovor)
      (netocan-odgovor))))


(defn matematika []
  [:div.pure-u-1
   [:h3 (str "Koliko je " (:a @state) " + " (:b @state) " ? ")
    [:input {:size 5
            ;;  :type :number
             :value (:c @state)
             :auto-focus true
             :on-change #(swap! state assoc :c (int (event-value %)))
             :on-key-up (fn [e]
                          (case (.toLowerCase (.-key e))
                            "enter" (check-math)
                            nil))}]]])

(defn naslov []
  [:h1.pure-u-1 (:naslov @state)])

(defn text []
  [:div.pure-u-1
   [:h4.pure-u-1 (:text @state)]])

(defn odgovori []
  [:div.pure-u-1
   (when (pos-int? (:tocnih @state))
     (let [uzastopni (:uzastopnih @state)]
       [:div
        (when (and (pos? uzastopni) (zero? (rem uzastopni 5)))
          [:h2 "Bravo! Imaš čak " uzastopni " točnih odgovora zaredom!"])
        [:p (str "Ukupno točnih odgovora: " (:tocnih @state))]
        [:p (str "Tocnih zaredom: " uzastopni)]]))
   [:table.pure-table-horizontal
    [:tbody
     (for [[i [a b c]] (map-indexed vector (:odgovori @state))]
       [:tr.pure-table {:key i}
        [:td a]
        [:td "+"]
        [:td b]
        [:td "="]
        [:td c]])]]])

(defn resetiraj []
  [:div.pure-u-1
   [:button.pure-button {:type "button"
                         :on-click #(reset! state initial-state)}
    "IZBRIŠI SVE"]])

(defn app []
  [:div
   [naslov]
   [matematika]
   [text]
   [:hr]
   [odgovori]
   [resetiraj]])

(defn mount-app-el []
  (rdom/render [app] (js/document.getElementById "app")))

(mount-app-el)

(defn ^:after-load on-reload []
  (mount-app-el))


(comment
  (println @state)
  ())