> Descriere de proiect

SITA XML este un instrument de extragere, transformare si incarcare a
datelor construit dupa o arhitectura MVC, destinat sa ruleze in regim de
proces(serviciu, daemon, etc) la intervale regulate pe o masina capabila
cu rol de server si care va interfata furnizorul de date si servicii de
telecomunicatii destinate transportului aerian(SITA) cu sistemul
RAPID/SAP in sensul incarcarii istorice(sau cronologice) a datelor
intr-o sursa(baza de date relationala) prestabilita, de unde vor fi
accesate de catre utilizatori in vederea realizarii de rapoarte
sintetice folosind instrumente predefinite sau customizate in-house.
Acest instrument trebuie sa raspunda necesitatilor interne de raportare
interna pe o durata de timp egala sau mai mare cu 30 de zile(o luna).

Modelul intern al aplicatiei se constituie dintr-o colectie de clase ale
caror instante-obiect sunt generate dinamic in memoria calculatorului de
parserul SAX care parcurge recursiv o structura arborescenta de noduri
si atribute(stocate intr-un fisier .XML). In favoarea adoptarii acestui
instrument a contat tipul de SGBD (Oracle) exploatat de compania noastra
si accesul direct(opus celui secvential/iterativ) pe care il ofera la
extragerea informatiei din atributele structurii XML. Acest model este
validat referential prin manipularea de atribute clasa si instanta de
tip intreg pana la finalul procesului de parsare(sfarsitul fisierului).
Procesul de generare a Modelului se constituie de fapt intr-o mapare
intuitiva(Schema XML -- un fisier descriptiv cu extensia .DTD nu mi-a
parvenit) a structurii arborescente intr-o structura relationala.

![](media/image1.png){width="6.104166666666667in"
height="2.3854166666666665in"}

Figura 1. Diagrama claselor reflecta Modelul obiectual intern

Componenta Controller este o supra-impunere a trei nivele, unul
responsabil cu extragerea propriu-zisa a datelor cu ajutorul SAXParser,
al doilea cu transformarea datelor in vederea tratarii exceptiilor (de
exemplu aparitia valorii null in campuri intregi) si convertirea datelor
(metodele convertData() si convertNumber()) conform tipului specificat
in modelul relational definit apriori in baza de date destinatie;
ultimul nivel responsabil cu incarcarea datelor apeleaza DriverManagerul
implicit(jdbc:odbc) cu specificarea sursei de date de tip User DSN
configurate si salvate local (de exemplu SITADB -- Microsoft Access
Driver) si executa instructiuni SQL de inserare parametrizate(de dorit
crearea de proceduri stocate pe server si apelarea acestora in timpul
executiei procesului, masura care va reduce timpul de incarcare a
datelor pe server).

![](media/image2.png){width="6.5in" height="2.84375in"}

Figura 2. Schema relationala a bazei de date(destinatie)

Componenta Viewer este implicita in SGBD Access sau Oracle, ambele
tipuri beneficiind de facilitati de raportare, dar poate fi definita
explicit pentru utilizarea in mediul online folosind limbajul
XSLT(pentru generarea de sabloane corespunzatoare fiecarui tip de
vizualizare/statistica) ori HTML in vederea generarii dinamice a
rapoartelor din sursa de date(folosind PHP on Server-side, fireste).

Cerinte tehnice in vederea implementarii proiectului:

-   spatiu de stocare si drepturi de acces pentru un **server de date**
    (MsSQLServer, Oracle, MySQL, samd.)

-   **server de proces** avand o capacitate de memorie interna de minim
    4GB, mecanisme robuste de paginare a memoriei virtuale si masina
    virtuala de Java instalata(JVM)

Resursele umane implicate permanent/temporar :

-   **Administrator** -- Cerinte:studii superioare de specialitate,
    experienta cu sisteme de operare in timp real, sisteme distribuite
    bazate pe tranzactii, cunostinte limbaj de programare Java;

-   **Reporting/Data analist** -- Cerinte: studii superioare de
    specialitate, cunostinte avansate limbaj SQL - analiza
    multidimensionala a datelor(Cube, Rollup ).

Resurse financiare: 0

**NOTA:**

In vederea stocarii datelor pentru perioade mai mari de un an recomandam
transferul periodic al datelor(3-6-9 luni) intr-o structura de tip
depozit de date avand urmatoarea structura:

![](media/image3.png){width="3.6666666666666665in"
height="3.4509798775153104in"}

Figura 3. Model de tip stea al depozitului de date
