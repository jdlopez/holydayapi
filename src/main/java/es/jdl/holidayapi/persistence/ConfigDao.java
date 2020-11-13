package es.jdl.holidayapi.persistence;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface ConfigDao {

    @Select("select entryKey, entryValue from configuration where entryKey like '${prefix}%'")
    @MapKey("entryKey")
    Map<String, KeyValueEntry> selectSubset(@Param("prefix") String prefix);

    class KeyValueEntry {
        public String entryKey;
        public String entryValue;
    }
}
