# HOLIDAY API

Holiday REST API. Service to get a list of days excluded from work-calendar.

> Because new Google's policy about GAE apps, no free of charge apps. Instead of blocking app if your quota goes out it charges you with the excess :-(

> I ported into heroku platform: [https://heroku.com/](https://heroku.com/)

Fully functional test environment: check it at: [https://holydayapi.herokuapp.com/](https://holydayapi.herokuapp.com/)

Actual focus: **Spanish holidays**

## Español

API para la consulta de calendarios laborales desplegado en un entorno abierto y gratuito.

> Si se abusa del uso tendré que limitarlo, por favor úsalo como fuente para tu propio almacenamiento.
No para llamadas directas.

_Prefijo para todas las llamadas:_
 
    https://holydayapi.herokuapp.com/
    
* **/holidays** consulta las vacaciones disponibles, dos formas principales:

* **/holidays/city/_nombre_** consulta por nombre de la ciudad _(atención: es literal)_

* **/holidays/city_code/_code_** consulta por nombre codigo INE del municipio

En ambos casos se acepta el sufijo **/year/_año_**

Ej.: 

    /holidays/city_code/50297/year/2019
    
Mostrará los días festivos correspondientes a la ciudad de Zaragoza, incluidos nacionales y de Aragón.

---

Los _"datos maestros"_ también se pueden consultar:
    
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

### Para Google Application Engine:

https://github.com/objectify/objectify/wiki/Setup#initialising-the-objectifyservice-to-work-with-emulator-applies-to-v6

    gcloud components install cloud-datastore-emulator
    
    gcloud beta emulators datastore start --host-port=localhost:8484 --data-dir=./target/data
    
    mvn appengine:run
    
>**watchout** "The Google Cloud Datastore emulator requires a Java 7+ JRE installed and on your system PATH" 
**1.8 not 10 or 11**
    
## Open Data. Sources

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
> * [CCAA](data/comunidades_autonomas.json)
> * [provincias](data/provincias.json)
> * [municipios](data/municipios.json)

## Configuracíón analytics

    https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide
    
## Deployment (heroku)

Just use standard git:

    https://devcenter.heroku.com/articles/git
    
    git push heroku master