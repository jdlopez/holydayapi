# HOLIDAY API

Holiday REST API. Service to get a list of days excluded from work-calendar.

Actual focus: **Spanish holidays**

> Changed from API to json repositories: https://github.com/jdlopez/open-data/tree/main/data/holidays/spain

## mini roadmap

* undersize application memory and process
* changed deployment to undertow
* changed database to H2
* upgrade some libraries and clear some configuration

## Español

API para la consulta de calendarios laborales desplegado en un entorno abierto y gratuito.

> Cambiada la idea para no depender de un hosting: repositorio de ficheros JSON en esta repo de github: https://github.com/jdlopez/open-data/tree/main/data/holidays/spain

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
    
