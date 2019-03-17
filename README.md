# HOLYDAY API

GAE app

TODO: explain a lot!!

## Test configuration

https://github.com/objectify/objectify/wiki/Setup#initialising-the-objectifyservice-to-work-with-emulator-applies-to-v6

    gcloud components install cloud-datastore-emulator
    
    gcloud beta emulators datastore start --host-port=localhost:8484
    
    mvn appengine:run
    
>**watchout** "The Google Cloud Datastore emulator requires a Java 7+ JRE installed and on your system PATH" 
**1.8 not 10 or 11**
    
## Data sources

* Ayuntamiento de madrid:

    https://datos.madrid.es/egob/catalogo/title/Calendario%20laboral.json

* Gobierno de aragon:

    https://opendata.aragon.es/ckan/api/action/resource_search?query=name:calendario%20de%20festivos
    
* Ciudad de Santander

    http://datos.santander.es/dataset/?id=calendario-laboral
    
### Servicios del catastro

http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx
    
* Provincias españolas (Catastro)

    http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx/ConsultaProvincia
    
* Municipios (Catastro)

    http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx?op=ConsultaMunicipioCodigos
    