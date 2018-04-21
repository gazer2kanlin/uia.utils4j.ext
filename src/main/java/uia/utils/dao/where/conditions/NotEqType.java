package uia.utils.dao.where.conditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class NotEqType implements ConditionType {
	
    private final String key;

    private final Object value;

    public NotEqType(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getStatement() {
        return this.value == null ? this.key + " is not null" : this.key + "<>?";
    }

    @Override
    public int accpet(PreparedStatement ps, int index) throws SQLException {
        if (this.value != null) {
        	if(this.value instanceof Date) {
                ps.setTimestamp(index++, new Timestamp(((Date)this.value).getTime()));
        	}
        	else {
                ps.setObject(index++, this.value);
        	}
        }
        return index;
    }
}
