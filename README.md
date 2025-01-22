# PO-PROJECT
Autorzy:
- Krzysztof Chmielewski
- Antoni Maślanka

# Wymagania
- [obowiązkowo dla wszystkich] kula ziemska - lewa i prawa krawędź mapy zapętlają się (jeżeli zwierzak wyjdzie za lewą krawędź, to pojawi się po prawej stronie - a jeżeli za prawą, to po lewej); górna i dolna krawędź mapy to bieguny - nie można tam wejść (jeżeli zwierzak próbuje wyjść poza te krawędzie mapy, to pozostaje na polu na którym był, a jego kierunek zmienia się na odwrotny);
- [obowiązkowo dla wszystkich] zalesione równiki - preferowany przez rośliny jest poziomy pas pól w centralnej części mapy (udający równik i okolice);
- [obowiązkowo dla wszystkich] pełna losowość - mutacja zmienia gen na dowolny inny gen;
- [obowiązkowo dla wszystkich] pełna predestynacja - zwierzak zawsze wykonuje kolejno geny, jeden po drugim;
---
- [G] dorodne plony - preferowany jest rozkład równomierny, ale na pewnym kwadratowym podobszarze mapy (zajmującym 20% mapy) czasem pojawiają się większe rośliny, których zjedzenie dodaje zwierzakowi znacznie więcej energii. Każda taka roślina zajmuje kwadratowy obszar 2x2 pola. Obsługa sytuacji, w której więcej zwierzaków kończy ruch na jednym z pól należących do dużej rośliny powinna wyglądać tak samo jak w przypadku, gdy wiele zwierząt walczy o normalną roślinę na jednym polu.
- [4] starość nie radość - starsze zwierzaki poruszają się wolniej, raz na kilka tur pomijając swój ruch, ale nadal tracąc energię. Prawdopodobieństwo pominięcia ruchu rośnie z wiekiem, maksymalnie do 80%.
