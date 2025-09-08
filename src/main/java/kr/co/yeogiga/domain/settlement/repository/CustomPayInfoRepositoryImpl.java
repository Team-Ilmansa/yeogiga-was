package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class CustomPayInfoRepositoryImpl implements CustomPayInfoRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_SQL
            = "INSERT INTO pay_info (user_id, price, is_completed, settlement_id) VALUES (?, ?, ?, ?)";
    
    @Override
    public void saveAllInBatch(List<PayInfo> payInfos) {
        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        PayInfo payInfo = payInfos.get(i);
                        ps.setLong(1, payInfo.getUserId());
                        ps.setLong(2, payInfo.getPrice());
                        ps.setBoolean(3, payInfo.isCompleted());
                        ps.setLong(4, payInfo.getSettlementId());
                    }
                    
                    @Override
                    public int getBatchSize() {
                        return payInfos.size();
                    }
                }
        );
    }
}
