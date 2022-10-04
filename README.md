# HOLIDAY API

Holiday REST API. Service to get a list of days excluded from work-calendar.

> New test server active. Please don't stress the service otherwise it will be stopped

http://jesusdavidlopez.hopto.org/holidayapi/

Actual focus: **Spanish holidays**

## mini roadmap

* undersize application memory and process
* changed deployment to undertow
* changed database to H2
* upgrade some libraries and clear some configuration

## Español

API para la consulta de calendarios laborales desplegado en un entorno abierto y gratuito.

> Nuevo servidor de pruebas, enlace: http://jesusdavidlopez.hopto.org/holidayapi/
> 
> Si se abusa del uso tendré que limitarlo, por favor úsalo como fuente para tu propio almacenamiento. No para llamadas directas.

_Prefijo para todas las llamadas:_
 
    http://jesusdavidlopez.hopto.org/holidayapi/
    
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
    
