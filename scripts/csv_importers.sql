insert into configuration (entryKey, entryValue)
values
    ('madrid.dateFormat', 'dd/MM/yyyy'),
    ('madrid.charSet', 'iso-8859-1'),
    ('madrid.delimiter', ';'),
    ('madrid.minColumns', '5'),
    ('madrid.dayIndex', '0'),
    ('madrid.checkHolydayRow', 'true'),
    ('madrid.holydayRowIndex', '2'),
    ('madrid.holydayRowTest', 'festivo'),
    ('madrid.typeColIndex', '3'),
    ('madrid.typeLocalTest', 'ciudad de Madrid'),
    ('madrid.typeRegionalTest', 'Comunidad de Madrid'),
    ('madrid.typeCountryTest', 'nacional'),
    ('madrid.checkGetLocal', 'false'),
    --('madrid.cityIndex',),
    ('madrid.skipRows', '1'),
    ('madrid.nameIndex', '4'),
    --('madrid.defaultType',)

    ('madrid.rfd', 'https://datos.madrid.es/egob/catalogo/title/Calendario%20laboral.json'),
    --('madrid.csvUrl', ''),
    ('madrid.country', 'ES'),
    ('madrid.region', '13'),
    ('madrid.city', '28079');

-- galicia
insert into configuration (entryKey, entryValue)
values
    ('galicia.dateFormat', 'dd/MM/yyyy'),
    ('galicia.charSet', 'iso-8859-1'),
    ('galicia.delimiter', ';'),
    ('galicia.minColumns', '5'),
    ('galicia.dayIndex', '0'),
    ('galicia.checkHolydayRow', 'false'),
    -- ('galicia.holydayRowIndex', ''),
    -- ('galicia.holydayRowTest', ''),
    ('galicia.typeColIndex', '2'),
    ('galicia.typeLocalTest', 'Local'),
    ('galicia.typeRegionalTest', 'autonómico'),
    ('galicia.typeCountryTest', 'estatal'),
    ('galicia.checkGetLocal', 'true'),
    ('galicia.cityIndex', '3'),
    ('galicia.skipRows', '2'),
    ('galicia.nameIndex', '1'),
    -- ('galicia.defaultType',)
    -- ('galicia.rfd', ''),
    ('galicia.csvUrl', 'https://abertos.xunta.gal/catalogo/economia-empresa-emprego/-/dataset/0403/calendario-laboral-2020/002/descarga-directa-ficheiro.csv'),
    ('galicia.country', 'ES'),
    ('galicia.region', '12')
    -- ('galicia.city', '')

