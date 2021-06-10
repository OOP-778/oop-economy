package net.milkbowl.vault.economy.plugins;

import com.oop.economy.OOPEconomyAPI;
import com.oop.economy.model.account.AccountModel;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Economy_OOP implements Economy {

  public Economy_OOP(Plugin plugin) {}

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "oop-economy";
  }

  @Override
  public boolean hasBankSupport() {
    return false;
  }

  @Override
  public int fractionalDigits() {
    return -1;
  }

  @Override
  public String format(double v) {
    return NumberFormat.getCurrencyInstance().format(v);
  }

  @Override
  public String currencyNamePlural() {
    return "";
  }

  @Override
  public String currencyNameSingular() {
    return "";
  }

  @Override
  public boolean hasAccount(String s) {
    return true;
  }

  @Override
  public boolean hasAccount(String s, String s1) {
    return hasAccount(s);
  }

  public double getBalance(OfflinePlayer player) {
    return account(player.getUniqueId()).getBalance().toDouble();
  }

  @Override
  public double getBalance(String s) {
    return OOPEconomyAPI.getAccountModel(s).getBalance().toDouble();
  }

  @Override
  public double getBalance(String s, String s1) {
    return getBalance(s);
  }

  @Override
  public boolean has(String s, double v) {
    return getBalance(s) >= v;
  }

  @Override
  public boolean has(String s, String s1, double v) {
    return has(s, v);
  }

  private AccountModel account(String unknown) {
    return OOPEconomyAPI.getAccountModel(unknown);
  }

  private AccountModel account(UUID uuid) {
    return OOPEconomyAPI.getAccountModel(uuid);
  }

  @Override
  public EconomyResponse withdrawPlayer(String s, double v) {
    AccountModel account = account(s);
    account.modifyBalance(current -> current.remove(v));

    return new EconomyResponse(
        v, account.getBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, "");
  }

  @Override
  public EconomyResponse withdrawPlayer(String s, String s1, double v) {
    return withdrawPlayer(s, v);
  }

  @Override
  public EconomyResponse depositPlayer(String s, double v) {
    AccountModel account = account(s);
    account.modifyBalance(current -> current.add(v));

    return new EconomyResponse(
        v, account.getBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, "");
  }

  @Override
  public EconomyResponse depositPlayer(String s, String s1, double v) {
    return depositPlayer(s, v);
  }

  @Override
  public EconomyResponse createBank(String s, String s1) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public EconomyResponse deleteBank(String s) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public EconomyResponse bankBalance(String s) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public EconomyResponse bankHas(String s, double v) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public EconomyResponse bankWithdraw(String s, double v) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public EconomyResponse bankDeposit(String s, double v) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public EconomyResponse isBankOwner(String s, String s1) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public EconomyResponse isBankMember(String s, String s1) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
  }

  @Override
  public List<String> getBanks() {
    return new ArrayList<>();
  }

  @Override
  public boolean createPlayerAccount(String s) {
    return true;
  }

  @Override
  public boolean createPlayerAccount(String s, String s1) {
    return createPlayerAccount(s);
  }
}
