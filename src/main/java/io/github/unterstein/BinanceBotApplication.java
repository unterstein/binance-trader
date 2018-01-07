package io.github.unterstein;

import com.binance.api.client.domain.account.AssetBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@SpringBootApplication
@RestController("/")
public class BinanceBotApplication {

  private static Logger logger = LoggerFactory.getLogger(BinanceBotApplication.class);

  private BinanceTrader trader;

  @Value("${TRADE_DIFFERENCE:0.00000001}")
  private double tradeDifference;

  @Value("${TRADE_PROFIT:1.3}")
  private double tradeProfit;

  @Value("${TRADE_AMOUNT:150}")
  private int tradeAmount;

  @Value("${BASE_CURRENCY:ETH}")
  private String baseCurrency;

  @Value("${TRADE_CURRENCY:XVG}")
  private String tradeCurrency;

  @Value("${API_KEY}")
  private String apiKey;

  @Value("${API_SECRET}")
  private String apiSecret;

  @PostConstruct
  public void init() {
    logger.info(String.format("Starting app with diff=%.8f, profit=%.8f amount=%d base=%s trade=%s", tradeDifference, tradeProfit, tradeAmount, baseCurrency, tradeCurrency));
    trader = new BinanceTrader(tradeDifference, tradeProfit, tradeAmount, baseCurrency, tradeCurrency, apiKey, apiSecret);
  }

  // tick every 3 seconds
  @Scheduled(fixedRate = 3000)
  public void schedule() {
    trader.tick();
  }

  @RequestMapping("/")
  public List<AssetBalance> getBalances() {
    return trader.getBalances().stream().filter(assetBalance -> !assetBalance.getFree().startsWith("0.0000")).collect(Collectors.toList());
  }

  public static void main(String[] args) {
    SpringApplication.run(BinanceBotApplication.class);
  }
}
