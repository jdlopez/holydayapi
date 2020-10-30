# HOLYDAY API

Holyday REST API. You can obtain a list of days excluded from work-calendar.

Deployed as Google Application Engine (Standard) Web Application.

You can check it at: [https://holydayapi.appspot.com/holydays](https://holydayapi.appspot.com/holydays)

Actual focus: Spanish holydays

## Español

API para la consulta de calendarios laborales desplegado en un entorno abierto y gratuito.

> Si se abusa del uso tendré que limitarlo, por favor úsalo como fuente para tu propio almacenamiento.
No para llamadas directas.

_Prefijo para todas las llamadas:_
 
    https://holydayapi.appspot.com/
    
* **/holydays** consulta las vacaciones disponibles, tiene varios parámetros
    - year : año de consulta, si no se especifica, toma el actual
    - country: código iso del país a consultar (por defecto ES)
    - province: código INE de la provincia, incluye también los festivos nacionales (en realidad los fija la Comunidad Autónoma)
    - city: código INE del municipio, se incluyen festivos nacionales y de la C.A.
    
* **/list/country**    consulta los paises. La lista se obtiene de los códigos de Locale de Java (guardados en bbdd)
    
    {
    "iso": "ES",
    "name": "SPAIN"
    }
    
* **/list/province**   consulta las provincias de España. Con su código del INE

    {
    "code": "02",
    "name": "ALBACETE",
    "country": "ES"
    }

* **/list/city**       consulta los municipios (no sólo ciudades) de una provincia. Hay que incluir un parameter _'prov'_

    {
    "code": "02001",
    "name": "ABENGIBRE"
    "province": "02",
    }
    

## Test configuration

https://github.com/objectify/objectify/wiki/Setup#initialising-the-objectifyservice-to-work-with-emulator-applies-to-v6

    gcloud components install cloud-datastore-emulator
    
    gcloud beta emulators datastore start --host-port=localhost:8484 --data-dir=./target/data
    
    mvn appengine:run
    
>**watchout** "The Google Cloud Datastore emulator requires a Java 7+ JRE installed and on your system PATH" 
**1.8 not 10 or 11**
    
## Data sources

* Calendarios CCAA (en PDF):

    https://administracion.gob.es/pag_Home/atencionCiudadana/calendarios/laboral.html

* Ayuntamiento de madrid (CSV):

    https://datos.madrid.es/egob/catalogo/title/Calendario%20laboral.json

* Ayuntamiento de Barcelona (ICS):

    https://ajuntament.barcelona.cat/calendarifestius/es/index.html
    
* Junta de Castilla y León

    https://datosabiertos.jcyl.es/web/jcyl/risp/es/empleo/laboral-cyl/1284165791785.csv
    
* Galicia

    https://abertos.xunta.gal/catalogo/economia-empresa-emprego/-/dataset/0403/calendario-laboral-2020/002/descarga-directa-ficheiro.csv

* Gobierno de aragon:

    https://opendata.aragon.es/ckan/api/action/resource_search?query=name:calendario%20de%20festivos
    
* Ciudad de Santander

    http://datos.santander.es/dataset/?id=calendario-laboral
    
* Euskadi

    https://opendata.euskadi.eus/contenidos/ds_eventos/calendario_laboral_2020/opendata/calendario_laboral_2020.csv
    
### Servicios del catastro

http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx
    
* Provincias españolas (Catastro)

    http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx/ConsultaProvincia
    
* Municipios (Catastro)

    http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx?op=ConsultaMunicipioCodigos

> Sustituido por versiones locales:
>
> * [provincias](data/provincias.json)
> * [municipios](data/municipios.json)
    