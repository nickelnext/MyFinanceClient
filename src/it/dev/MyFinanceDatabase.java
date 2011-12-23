package it.dev;

import Quotes.Quotation_Bond;
import Quotes.Quotation_Fund;
import Quotes.Quotation_Share;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyFinanceDatabase 
{
	SQLiteDatabase database;
	DatabaseHelper databaseHelper;
	Context context;
	private static final String DB_NAME = "MyFinanceDatabase";
	private static final int DB_VERSION = 1;
	
	public MyFinanceDatabase(Context ctx)
	{
		this.context = ctx;
		databaseHelper = new DatabaseHelper(ctx, DB_NAME, null, DB_VERSION);
	}
	
	public void open()
	{
		database = databaseHelper.getWritableDatabase();
	}
	
	public void close()
	{
		database.close();
	}
	
	//---------------------------------------METADATA delle tabelle---------------------------//
	//--------richiamabili in ogni punto dell'app, per ottenere le colonne delle tabelle------//
	static class PortfolioMetaData
	{
		static final String PORTFOLIO_TABLE = "Table_Portfolio";
		static final String ID = "_id";
		static final String PORTFOLIO_NAME_KEY = "nome";
		static final String PORTFOLIO_DESCRIPTION_KEY ="descrizione";
		static final String PORTFOLIO_CREATION_DATE_KEY = "dataCreazione";
	}
	
	static class BondMetaData
	{
		static final String BOND_TABLE = "Quotation_Bond";
		static final String ID = "_id";
		static final String BOND_ISIN = "isin";
		static final String BOND_NAME_KEY = "nome";
		static final String BOND_CURRENCY_KEY = "valuta";
		static final String BOND_MARKET_KEY = "mercato";
		static final String BOND_MARKETPHASE_KEY = "faseMercato";
		static final String BOND_LASTCONTRACTPRICE_KEY = "prezzoUltimoContratto";
		static final String BOND_PERCVAR_KEY = "variazionePercentuale";
		static final String BOND_VARIATION_KEY = "variazioneAssoluta";
		static final String BOND_LASTCONTRACTDATE_KEY = "dataUltimoContratto";
		static final String BOND_LASTVOLUME_KEY = "volumeUltimo";
		static final String BOND_BUYVOLUME_KEY = "volumeAcquisto";
		static final String BOND_SELLVOLUME_KEY = "volumeVendita";
		static final String BOND_TOTALVOLUME_KEY = "volumeTotale";
		static final String BOND_BUYPRICE_KEY = "prezzoAcquisto";
		static final String BOND_SELLPRICE_KEY = "prezzoVendita";
		static final String BOND_MAXTODAY_KEY = "maxOggi";
		static final String BOND_MINTODAY_KEY = "minOggi";
		static final String BOND_MAXYEAR_KEY = "maxAnno";
		static final String BOND_MINYEAR_KEY = "minAnno";
		static final String BOND_MAXYEARDATE_KEY = "dataMaxAnno";
		static final String BOND_MINYEARDATE_KEY = "dataMinAnno";
		static final String BOND_LASTCLOSE_KEY = "aperturaChiusuraPrecedente";
		static final String BOND_EXPIRATIONDATE_KEY = "scadenza";
		static final String BOND_COUPONDATE_KEY = "dataStaccoCedola";
		static final String BOND_COUPON_KEY = "cedola";
		static final String BOND_MINROUNDLOT_KEY = "lottoMinimo";
		static final String BOND_LASTUPDATE_KEY = "dataUltimoAggiornamento";		
	}
	
	static class FundMetaData
	{
		static final String FUND_TABLE = "Quotation_Fund";
		static final String ID = "_id";
		static final String FUND_ISIN = "isin";
		static final String FUND_NAME_KEY = "nome";
		static final String FUND_MANAGER_KEY = "nomeGestore";
		static final String FUND_CATEGORY_KEY = "categoriaAssociati";
		static final String FUND_BENCHMARK_KEY = "benchmarkDichiarato";
		static final String FUND_LASTPRIZE_KEY = "ultimoPrezzo";
		static final String FUND_LASTPRIZEDATE_KEY = "dataUltimoPrezzo";
		static final String FUND_PRECPRIZE_KEY = "prezzoPrecedente";
		static final String FUND_CURRENCY_KEY = "valuta";
		static final String FUND_PERCVAR_KEY = "variazionePercentuale";
		static final String FUND_VARIATION_KEY = "variazioneAssoluta";
		static final String FUND_PERFORMANCE1MONTH = "performance1Mese";
		static final String FUND_PERFORMANCE3MONTH = "performance3Mese";
		static final String FUND_PERFORMANCE1YEAR = "performance1Year";
		static final String FUND_PERFORMANCE3YEAR = "performance3Year";
		static final String FUND_LASTUPDATE_KEY = "ultimoAggiornamento";
	}
	
	static class ShareMetaData
	{
		static final String SHARE_TABLE = "Quotation_Share";
		static final String ID = "_id";
		static final String SHARE_CODE = "codice";
		static final String SHARE_NAME_KEY = "nome";
		static final String SHARE_MINROUNDLOT_KEY = "lottoMinimo";
		static final String SHARE_MARKETPHASE_KEY = "faseMercato";
		static final String SHARE_LASTCONTRACTPRICE_KEY = "prezzoUltimoContratto";
		static final String SHARE_PERCVAR_KEY = "variazionePercentuale";
		static final String SHARE_VARIATION_KEY = "variazioneAssoluta";
		static final String SHARE_LASTCONTRACTDATE_KEY = "dataOraUltimoAcquisto";
		static final String SHARE_BUYPRICE_KEY = "prezzoAcquisto";
		static final String SHARE_SELLPRICE_KEY = "prezzoVendita";
		static final String SHARE_LASTAMOUNT_KEY = "quantitaUltimo";
		static final String SHARE_BUYAMOUNT_KEY = "quantitaAcquisto";
		static final String SHARE_SELLAMOUNT_KEY = "quantitaVendita";
		static final String SHARE_TOTALAMOUNT_KEY = "quantitaTotale";
		static final String SHARE_MAXTODAY_KEY = "maxOggi";
		static final String SHARE_MINTODAY_KEY = "minOggi";
		static final String SHARE_MAXYEAR_KEY = "maxAnno";
		static final String SHARE_MINYEAR_KEY = "minAnno";
		static final String SHARE_MAXYEARDATE_KEY = "dataMaxAnno";
		static final String SHARE_MINYEARDATE_KEY = "dataMinAnno";
		static final String SHARE_LASTCLOSE_KEY = "chiusuraPrecedente";
		static final String SHARE_LASTUPDATE_KEY = "dataUltimoAggiornamento";
	}
	
	//-----------------------------------METADATA tabelle di transizione-----------------------------//
	static class PortfolioBondMetadata
	{
		static final String PORTFOLIO_BOND_TABLE = "Table_Portfolio_Bond";
		static final String ID = "_id";
		static final String PORTFOLIO_NAME_KEY = "nomePortafoglio";
		static final String BOND_ISIN_KEY = "isinObbligazione";
		static final String BOND_BUYDATE_KEY = "dataAcquisto";
		static final String BOND_BUYPRICE_KEY = "prezzoAcquisto";
		static final String BOND_ROUNDLOT_KEY = "lotto";
		static final String BOND_CAPITALGAINTAX_KEY = "tassaCapitalGain";
		static final String BOND_COUPONTAX_KEY = "tassaCedola";
	}
	
	static class PortfolioFundMetadata
	{
		static final String PORTFOLIO_FUND_TABLE = "Table_Portfolio_Fund";
		static final String ID = "_id";
		static final String PORTFOLIO_NAME_KEY = "nomePortafoglio";
		static final String FUND_ISIN_KEY = "isinFondo";
		static final String FUND_BUYDATE_KEY = "dataAcquisto";
		static final String FUND_BUYPRICE_KEY = "prezzoAcquisto";
		static final String FUND_ROUNDLOT_KEY = "lotto";
		static final String FUND_CAPITALGAINTAX_KEY = "tassaCapitalGain";
		static final String FUND_COUPONTAX_KEY = "tassaCedola";
	}
	
	static class PortfolioShareMetadata
	{
		static final String PORTFOLIO_SHARE_TABLE = "Table_Portfolio_Share";
		static final String ID = "_id";
		static final String PORTFOLIO_NAME_KEY = "nomePortafoglio";
		static final String SHARE_CODE_KEY = "codiceAzione";
		static final String SHARE_BUYDATE_KEY = "dataAcquisto";
		static final String SHARE_BUYPRICE_KEY = "prezzoAcquisto";
		static final String SHARE_ROUNDLOT_KEY = "lotto";
		static final String SHARE_CAPITALGAINTAX_KEY = "tassaCapitalGain";
		static final String SHARE_COUPONTAX_KEY = "tassaCedola";
	}
	
	//-------------------------------------STRING per creazione tabelle-------------------------------//
	private static final String TABLE_PORTFOLIO_CREATE = "CREATE TABLE "+PortfolioMetaData.PORTFOLIO_TABLE+ " (" +
			PortfolioMetaData.ID +" INTEGER NOT NULL, " +
			PortfolioMetaData.PORTFOLIO_NAME_KEY +" TEXT PRIMARY KEY, " +
			PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY +" TEXT NOT NULL, " +
			PortfolioMetaData.PORTFOLIO_CREATION_DATE_KEY +" TEXT NOT NULL);";
	
	private static final String TABLE_BOND_CREATE = "CREATE TABLE "+BondMetaData.BOND_TABLE+" (" +
			BondMetaData.ID +" INTEGER NOT NULL, " +
			BondMetaData.BOND_ISIN +" TEXT PRIMARY KEY, " +
			BondMetaData.BOND_NAME_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_CURRENCY_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_MARKET_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_MARKETPHASE_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_LASTCONTRACTPRICE_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_PERCVAR_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_VARIATION_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_LASTCONTRACTDATE_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_LASTVOLUME_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_BUYVOLUME_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_SELLVOLUME_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_TOTALVOLUME_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_BUYPRICE_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_SELLPRICE_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_MAXTODAY_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_MINTODAY_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_MAXYEAR_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_MINYEAR_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_MAXYEARDATE_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_MINYEARDATE_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_LASTCLOSE_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_EXPIRATIONDATE_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_COUPONDATE_KEY +" TEXT NOT NULL, " +
			BondMetaData.BOND_COUPON_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_MINROUNDLOT_KEY +" INTEGER NOT NULL, " +
			BondMetaData.BOND_LASTUPDATE_KEY +" TEXT NOT NULL);";
	
	private static final String TABLE_FUND_CREATE = "CREATE TABLE "+FundMetaData.FUND_TABLE+" (" +
			FundMetaData.ID +" INTEGER NOT NULL, " +
			FundMetaData.FUND_ISIN +" TEXT PRIMARY KEY, " +
			FundMetaData.FUND_NAME_KEY +" TEXT NOT NULL, " +
			FundMetaData.FUND_MANAGER_KEY +" TEXT NOT NULL, " +
			FundMetaData.FUND_CATEGORY_KEY +" TEXT NOT NULL, " +
			FundMetaData.FUND_BENCHMARK_KEY +" TEXT NOT NULL, " +
			FundMetaData.FUND_LASTPRIZE_KEY +" INTEGER NOT NULL, " +
			FundMetaData.FUND_LASTPRIZEDATE_KEY +" TEXT NOT NULL, " +
			FundMetaData.FUND_PRECPRIZE_KEY +" INTEGER NOT NULL, " +
			FundMetaData.FUND_CURRENCY_KEY +" TEXT NOT NULL, " +
			FundMetaData.FUND_PERCVAR_KEY +" INTEGER NOT NULL, " +
			FundMetaData.FUND_VARIATION_KEY +" INTEGER NOT NULL, " +
			FundMetaData.FUND_PERFORMANCE1MONTH +" INTEGER NOT NULL, " +
			FundMetaData.FUND_PERFORMANCE3MONTH +" INTEGER NOT NULL, " +
			FundMetaData.FUND_PERFORMANCE1YEAR +" INTEGER NOT NULL, " +
			FundMetaData.FUND_PERFORMANCE3YEAR +" INTEGER NOT NULL, " +
			FundMetaData.FUND_LASTUPDATE_KEY +" TEXT NOT NULL);";
	
	private static final String TABLE_SHARE_CREATE = "CREATE TABLE "+ShareMetaData.SHARE_TABLE+" (" +
			ShareMetaData.ID +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_CODE +" TEXT PRIMARY KEY, " +
			ShareMetaData.SHARE_NAME_KEY +" TEXT NOT NULL, " +
			ShareMetaData.SHARE_MINROUNDLOT_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_MARKETPHASE_KEY +" TEXT NOT NULL, " +
			ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_PERCVAR_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_VARIATION_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_LASTCONTRACTDATE_KEY +" TEXT NOT NULL, " +
			ShareMetaData.SHARE_BUYPRICE_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_SELLPRICE_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_LASTAMOUNT_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_BUYAMOUNT_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_SELLAMOUNT_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_TOTALAMOUNT_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_MAXTODAY_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_MINTODAY_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_MAXYEAR_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_MINYEAR_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_MAXYEARDATE_KEY +" TEXT NOT NULL, " +
			ShareMetaData.SHARE_MINYEARDATE_KEY +" TEXT NOT NULL, " +
			ShareMetaData.SHARE_LASTCLOSE_KEY +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_LASTUPDATE_KEY +" TEXT NOT NULL);";
	
	//-------------------------------------STRING per creazione tabelle di transizione-------------------//
	private static final String TABLE_PORTFOLIO_BOND_CREATE = "CREATE TABLE "+PortfolioBondMetadata.PORTFOLIO_BOND_TABLE+" (" +
			PortfolioBondMetadata.ID +" INTEGER NOT NULL, " +
			PortfolioBondMetadata.PORTFOLIO_NAME_KEY +" TEXT NOT NULL, " +
			PortfolioBondMetadata.BOND_ISIN_KEY +" TEXT NOT NULL, " +
			PortfolioBondMetadata.BOND_BUYDATE_KEY +" TEXT NOT NULL, " +
			PortfolioBondMetadata.BOND_BUYPRICE_KEY +" INTEGER NOT NULL, " +
			PortfolioBondMetadata.BOND_ROUNDLOT_KEY +" INTEGER NOT NULL, " +
			PortfolioBondMetadata.BOND_CAPITALGAINTAX_KEY +" INTEGER NOT NULL, " +
			PortfolioBondMetadata.BOND_COUPONTAX_KEY +" INTEGER NOT NULL, " +
			"PRIMARY KEY (" +PortfolioBondMetadata.PORTFOLIO_NAME_KEY+", " +PortfolioBondMetadata.BOND_ISIN_KEY+", " +PortfolioBondMetadata.BOND_BUYDATE_KEY+"), " +
			"FOREIGN KEY (" +PortfolioBondMetadata.PORTFOLIO_NAME_KEY+") REFERENCES "+PortfolioMetaData.PORTFOLIO_TABLE+"("+PortfolioMetaData.PORTFOLIO_NAME_KEY+")" +
			"FOREIGN KEY (" +PortfolioBondMetadata.BOND_ISIN_KEY+") REFERENCES "+BondMetaData.BOND_TABLE+"("+BondMetaData.BOND_ISIN+"));";
	
	private static final String TABLE_PORTFOLIO_FUND_CREATE = "CREATE TABLE "+PortfolioFundMetadata.PORTFOLIO_FUND_TABLE+" (" +
			PortfolioFundMetadata.ID +" INTEGER NOT NULL, " +
			PortfolioFundMetadata.PORTFOLIO_NAME_KEY +" TEXT NOT NULL, " +
			PortfolioFundMetadata.FUND_ISIN_KEY +" TEXT NOT NULL, " +
			PortfolioFundMetadata.FUND_BUYDATE_KEY +" TEXT NOT NULL, " +
			PortfolioFundMetadata.FUND_BUYPRICE_KEY +" INTEGER NOT NULL, " +
			PortfolioFundMetadata.FUND_ROUNDLOT_KEY +" INTEGER NOT NULL, " +
			PortfolioFundMetadata.FUND_CAPITALGAINTAX_KEY +" INTEGER NOT NULL, " +
			PortfolioFundMetadata.FUND_COUPONTAX_KEY +" INTEGER NOT NULL, " +
			"PRIMARY KEY (" +PortfolioFundMetadata.PORTFOLIO_NAME_KEY+", " +PortfolioFundMetadata.FUND_ISIN_KEY+", " +PortfolioFundMetadata.FUND_BUYDATE_KEY+"), " +
			"FOREIGN KEY (" +PortfolioFundMetadata.PORTFOLIO_NAME_KEY+") REFERENCES "+PortfolioMetaData.PORTFOLIO_TABLE+"("+PortfolioMetaData.PORTFOLIO_NAME_KEY+")" +
			"FOREIGN KEY (" +PortfolioFundMetadata.FUND_ISIN_KEY+") REFERENCES "+FundMetaData.FUND_TABLE+"("+FundMetaData.FUND_ISIN+"));";
	
	private static final String TABLE_PORTFOLIO_SHARE_CREATE = "CREATE TABLE "+PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE+" (" +
			PortfolioShareMetadata.ID +" INTEGER NOT NULL, " +
			PortfolioShareMetadata.PORTFOLIO_NAME_KEY +" TEXT NOT NULL, " +
			PortfolioShareMetadata.SHARE_CODE_KEY +" TEXT NOT NULL, " +
			PortfolioShareMetadata.SHARE_BUYDATE_KEY +" TEXT NOT NULL, " +
			PortfolioShareMetadata.SHARE_BUYPRICE_KEY +" INTEGER NOT NULL, " +
			PortfolioShareMetadata.SHARE_ROUNDLOT_KEY +" INTEGER NOT NULL, " +
			PortfolioShareMetadata.SHARE_CAPITALGAINTAX_KEY +" INTEGER NOT NULL, " +
			PortfolioShareMetadata.SHARE_COUPONTAX_KEY +" INTEGER NOT NULL, " +
			"PRIMARY KEY (" +PortfolioShareMetadata.PORTFOLIO_NAME_KEY+", " +PortfolioShareMetadata.SHARE_CODE_KEY+", " +PortfolioShareMetadata.SHARE_BUYDATE_KEY+"), " +
			"FOREIGN KEY (" +PortfolioShareMetadata.PORTFOLIO_NAME_KEY+") REFERENCES "+PortfolioMetaData.PORTFOLIO_TABLE+"("+PortfolioMetaData.PORTFOLIO_NAME_KEY+")" +
			"FOREIGN KEY (" +PortfolioShareMetadata.SHARE_CODE_KEY+") REFERENCES "+ShareMetaData.SHARE_TABLE+"("+ShareMetaData.SHARE_CODE+"));";
	
	//-------------------------------------HELPER class---------------------------------------//
	private class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context, String name, CursorFactory factory, int version)
		{
			super(context, name, factory, version);
		}
		
		public void onCreate(SQLiteDatabase _db) 
		{ 
			Log.d(DB_NAME, TABLE_PORTFOLIO_CREATE);
			//per ogni tabella nel database facciamo una execSQL con la relativa stringa di ceazione
            _db.execSQL(TABLE_PORTFOLIO_CREATE);
            _db.execSQL(TABLE_BOND_CREATE);            
            _db.execSQL(TABLE_FUND_CREATE);
            _db.execSQL(TABLE_SHARE_CREATE);
            _db.execSQL(TABLE_PORTFOLIO_BOND_CREATE);
            _db.execSQL(TABLE_PORTFOLIO_FUND_CREATE);
            _db.execSQL(TABLE_PORTFOLIO_SHARE_CREATE);
            
            //debug
            Log.d(DB_NAME, TABLE_PORTFOLIO_CREATE); 
            Log.d(DB_NAME, TABLE_BOND_CREATE); 
            Log.d(DB_NAME, TABLE_FUND_CREATE); 
            Log.d(DB_NAME, TABLE_SHARE_CREATE); 
            Log.d(DB_NAME, TABLE_PORTFOLIO_BOND_CREATE);
            Log.d(DB_NAME, TABLE_PORTFOLIO_FUND_CREATE);
            Log.d(DB_NAME, TABLE_PORTFOLIO_SHARE_CREATE);
		}
		
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) 
		{
            //qui mettiamo eventuali modifiche al db, se nella nostra nuova versione della app, il db cambia numero di versione
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//--------------------------------------------------------------------------//
	//------------------------INTERROGAZIONI AL DATABASE------------------------//
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//////////////////////////////////////////////////////////////////////////////
	
	//--------------------------------INSERT methods----------------------------//
	public void addNewPortfolio(int _id, String name, String description, String creationDate)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioMetaData.ID, _id);
		cv.put(PortfolioMetaData.PORTFOLIO_NAME_KEY, name);
		cv.put(PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY, description);
		cv.put(PortfolioMetaData.PORTFOLIO_CREATION_DATE_KEY, creationDate);
		database.insert(PortfolioMetaData.PORTFOLIO_TABLE, null, cv);
	}
	
	public void addNewBond(int _id, String isin, String name, String currency, String market, String marketPhase, float lastContractPrice, 
			float percVariation, float variation, String lastContractDate, int lastVolume, int buyVolume, int sellVolume, 
			int totalVolume, float buyPrice, float sellPrice, float maxToday, float minToday, float maxYear, float minYear, 
			String maxYearDate, String minYearDate, float lastClose, String expirationDate, String couponDate, float coupon, 
			int minRoundLot, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(BondMetaData.ID, _id);
		cv.put(BondMetaData.BOND_ISIN, isin);
		cv.put(BondMetaData.BOND_NAME_KEY, name);
		cv.put(BondMetaData.BOND_CURRENCY_KEY, currency);
		cv.put(BondMetaData.BOND_MARKET_KEY, market);
		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, marketPhase);
		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, lastContractPrice);
		cv.put(BondMetaData.BOND_PERCVAR_KEY, percVariation);
		cv.put(BondMetaData.BOND_VARIATION_KEY, variation);
		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, lastContractDate);
		cv.put(BondMetaData.BOND_LASTVOLUME_KEY, lastVolume);
		cv.put(BondMetaData.BOND_BUYVOLUME_KEY, buyVolume);
		cv.put(BondMetaData.BOND_SELLVOLUME_KEY, sellVolume);
		cv.put(BondMetaData.BOND_TOTALVOLUME_KEY, totalVolume);
		cv.put(BondMetaData.BOND_BUYPRICE_KEY, buyPrice);
		cv.put(BondMetaData.BOND_SELLPRICE_KEY, sellPrice);
		cv.put(BondMetaData.BOND_MAXTODAY_KEY, maxToday);
		cv.put(BondMetaData.BOND_MINTODAY_KEY, minToday);
		cv.put(BondMetaData.BOND_MAXYEAR_KEY, maxYear);
		cv.put(BondMetaData.BOND_MINYEAR_KEY, minYear);
		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, maxYearDate);
		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, minYearDate);
		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, lastClose);
		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, expirationDate);
		cv.put(BondMetaData.BOND_COUPONDATE_KEY, couponDate);
		cv.put(BondMetaData.BOND_COUPON_KEY, coupon);
		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, minRoundLot);
		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
		database.insert(BondMetaData.BOND_TABLE, null, cv);
	}
	
	public void addNewBondByQuotationObject(Quotation_Bond newBond, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(BondMetaData.ID, 1);
		cv.put(BondMetaData.BOND_ISIN, ((Quotes.Quotation)newBond).getISIN());
		cv.put(BondMetaData.BOND_NAME_KEY, newBond.getName());
		cv.put(BondMetaData.BOND_CURRENCY_KEY, newBond.getValuta());
		cv.put(BondMetaData.BOND_MARKET_KEY, newBond.getMercato());
		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, newBond.getFaseMercato());
		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, newBond.getPrezzoUltimoContratto());
		cv.put(BondMetaData.BOND_PERCVAR_KEY, newBond.getVariazionePercentuale());
		cv.put(BondMetaData.BOND_VARIATION_KEY, newBond.getVariazioneAssoluta());
		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, newBond.getDataUltimoContratto().toString());
		cv.put(BondMetaData.BOND_LASTVOLUME_KEY, newBond.getVolumeUltimo());
		cv.put(BondMetaData.BOND_BUYVOLUME_KEY, newBond.getVolumeAcquisto());
		cv.put(BondMetaData.BOND_SELLVOLUME_KEY, newBond.getVolumeVendita());
		cv.put(BondMetaData.BOND_TOTALVOLUME_KEY, newBond.getVolumeTotale());
		cv.put(BondMetaData.BOND_BUYPRICE_KEY, newBond.getPrezzoAcquisto());
		cv.put(BondMetaData.BOND_SELLPRICE_KEY, newBond.getPrezzoVendita());
		cv.put(BondMetaData.BOND_MAXTODAY_KEY, newBond.getMaxOggi());
		cv.put(BondMetaData.BOND_MINTODAY_KEY, newBond.getMinOggi());
		cv.put(BondMetaData.BOND_MAXYEAR_KEY, newBond.getMaxAnno());
		cv.put(BondMetaData.BOND_MINYEAR_KEY, newBond.getMinAnno());
		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, newBond.getDataMaxAnno().toString());
		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, newBond.getDataMinAnno().toString());
		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, newBond.getAperturaChiusuraPrecedente());
		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, newBond.getScadenza().toString());		
		cv.put(BondMetaData.BOND_COUPONDATE_KEY, newBond.getDataStaccoCedola().toString());
		System.out.println("cedola: "+newBond.getDataStaccoCedola());
		cv.put(BondMetaData.BOND_COUPON_KEY, newBond.getCedola());
		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, newBond.getLottoMinimo());
		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
		database.insert(BondMetaData.BOND_TABLE, null, cv);
	}
	
	public void addNewFund(int _id, String isin, String name, String manager, String category, String benchmark, 
			float lastPrize, String lastPrizeDate, float precPrize, String currency, float percVariation, float variation, 
			float performance1Month, float performance3Month, float performance1Year, float performance3Year, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(FundMetaData.ID, _id);
		cv.put(FundMetaData.FUND_ISIN, isin);
		cv.put(FundMetaData.FUND_NAME_KEY, name);
		cv.put(FundMetaData.FUND_MANAGER_KEY, manager);
		cv.put(FundMetaData.FUND_CATEGORY_KEY, category);
		cv.put(FundMetaData.FUND_BENCHMARK_KEY, benchmark);
		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, lastPrize);
		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, lastPrizeDate);
		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, precPrize);
		cv.put(FundMetaData.FUND_CURRENCY_KEY, currency);
		cv.put(FundMetaData.FUND_PERCVAR_KEY, percVariation);
		cv.put(FundMetaData.FUND_VARIATION_KEY, variation);
		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, performance1Month);
		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, performance3Month);
		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, performance1Year);
		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, performance3Year);
		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
		database.insert(FundMetaData.FUND_TABLE, null, cv);
	}
	
	public void addNewFundByQuotationObject(Quotation_Fund newFund, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(FundMetaData.ID, 1);
		cv.put(FundMetaData.FUND_ISIN, ((Quotes.Quotation)newFund).getISIN());
		cv.put(FundMetaData.FUND_NAME_KEY, newFund.getName());
		cv.put(FundMetaData.FUND_MANAGER_KEY, newFund.getNomeGestore());
		cv.put(FundMetaData.FUND_CATEGORY_KEY, newFund.getCategoriaAssociati());
		cv.put(FundMetaData.FUND_BENCHMARK_KEY, newFund.getBenchmarkDichiarato());
		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, newFund.getUltimoPrezzo());
		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, newFund.getDataUltimoPrezzo().toGMTString());
		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, newFund.getPrezzoPrecedente());
		cv.put(FundMetaData.FUND_CURRENCY_KEY, newFund.getValuta());
		cv.put(FundMetaData.FUND_PERCVAR_KEY, newFund.getVariazionePercentuale());
		cv.put(FundMetaData.FUND_VARIATION_KEY, newFund.getVariazioneAssoluta());
		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, newFund.getPerformance1Mese());
		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, newFund.getPerformance3Mesi());
		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, newFund.getPerformance1Anno());
		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, newFund.getPerformance3Anni());
		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
		database.insert(FundMetaData.FUND_TABLE, null, cv);
	}
	
	public void addNewShare(int _id, String code, String name, int minRoundLot, String marketPhase, float lastContractPrice, 
			float percVariation, float variation, String lastContractDate, float buyPrice, float sellPrice, int lastAmount, 
			int buyAmount, int sellAmount, int totalAmount, float maxToday, float minToday, float maxYear, float minYear, 
			String maxYearDate, String minYearDate, float lastClose, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.ID, _id);
		cv.put(ShareMetaData.SHARE_CODE, code);
		cv.put(ShareMetaData.SHARE_NAME_KEY, name);
		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, minRoundLot);
		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, marketPhase);
		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, lastContractPrice);
		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, percVariation);
		cv.put(ShareMetaData.SHARE_VARIATION_KEY, variation);
		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, lastContractDate);
		cv.put(ShareMetaData.SHARE_BUYPRICE_KEY, buyPrice);
		cv.put(ShareMetaData.SHARE_SELLPRICE_KEY, sellPrice);
		cv.put(ShareMetaData.SHARE_LASTAMOUNT_KEY, lastAmount);
		cv.put(ShareMetaData.SHARE_BUYAMOUNT_KEY, buyAmount);
		cv.put(ShareMetaData.SHARE_SELLAMOUNT_KEY, sellAmount);
		cv.put(ShareMetaData.SHARE_TOTALAMOUNT_KEY, totalAmount);
		cv.put(ShareMetaData.SHARE_MAXTODAY_KEY, maxToday);
		cv.put(ShareMetaData.SHARE_MINTODAY_KEY, minToday);
		cv.put(ShareMetaData.SHARE_MAXYEAR_KEY, maxYear);
		cv.put(ShareMetaData.SHARE_MINYEAR_KEY, minYear);
		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, maxYearDate);
		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, minYearDate);
		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, lastClose);
		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
		database.insert(ShareMetaData.SHARE_TABLE, null, cv);
	}
	
	public void addNewShareByQuotationObject(Quotation_Share newShare, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.ID, 1);
		cv.put(ShareMetaData.SHARE_CODE, ((Quotes.Quotation)newShare).getISIN());
		cv.put(ShareMetaData.SHARE_NAME_KEY, newShare.getName());
		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, newShare.getLottoMinimo());
		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, newShare.getFaseMercato());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, newShare.getPrezzoUltimoContratto());
		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, newShare.getVariazionePercentuale());
		cv.put(ShareMetaData.SHARE_VARIATION_KEY, newShare.getVariazioneAssoluta());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, newShare.getDataOraUltimoAcquisto().toGMTString());
		cv.put(ShareMetaData.SHARE_BUYPRICE_KEY, newShare.getPrezzoAcquisto());
		cv.put(ShareMetaData.SHARE_SELLPRICE_KEY, newShare.getPrezzoVendita());
		cv.put(ShareMetaData.SHARE_LASTAMOUNT_KEY, newShare.getQuantitaUltimo());
		cv.put(ShareMetaData.SHARE_BUYAMOUNT_KEY, newShare.getQuantitaAcquisto());
		cv.put(ShareMetaData.SHARE_SELLAMOUNT_KEY, newShare.getQuantitaVendita());
		cv.put(ShareMetaData.SHARE_TOTALAMOUNT_KEY, newShare.getQuantitaTotale());
		cv.put(ShareMetaData.SHARE_MAXTODAY_KEY, newShare.getMaxOggi());
		cv.put(ShareMetaData.SHARE_MINTODAY_KEY, newShare.getMinOggi());
		cv.put(ShareMetaData.SHARE_MAXYEAR_KEY, newShare.getMaxAnno());
		cv.put(ShareMetaData.SHARE_MINYEAR_KEY, newShare.getMinAnno());
		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, newShare.getDataMaxAnno().toGMTString());
		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, newShare.getDataMinAnno().toGMTString());
		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, newShare.getChiusuraPrecedente());
		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
		database.insert(ShareMetaData.SHARE_TABLE, null, cv);
	}
	
	public void addNewBondInTransitionTable(String portfolioName, String bondISIN, String buyDate, float buyPrice, 
			int roundLot, float capitalGainTax, float couponTax)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioBondMetadata.ID, 1);
		cv.put(PortfolioBondMetadata.PORTFOLIO_NAME_KEY, portfolioName);
		cv.put(PortfolioBondMetadata.BOND_ISIN_KEY, bondISIN);
		cv.put(PortfolioBondMetadata.BOND_BUYDATE_KEY, buyDate);
		cv.put(PortfolioBondMetadata.BOND_BUYPRICE_KEY, buyPrice);
		cv.put(PortfolioBondMetadata.BOND_ROUNDLOT_KEY, roundLot);
		cv.put(PortfolioBondMetadata.BOND_CAPITALGAINTAX_KEY, capitalGainTax);
		cv.put(PortfolioBondMetadata.BOND_COUPONTAX_KEY, couponTax);
		database.insert(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, null, cv);
	}
	
	public void addNewFoundInTransitionTable(String portfolioName, String fundISIN, String buyDate, float buyPrice, int roundLot, float capitalGainTax, float couponTax)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioFundMetadata.ID, 1);
		cv.put(PortfolioFundMetadata.PORTFOLIO_NAME_KEY, portfolioName);
		cv.put(PortfolioFundMetadata.FUND_ISIN_KEY, fundISIN);
		cv.put(PortfolioFundMetadata.FUND_BUYDATE_KEY, buyDate);
		cv.put(PortfolioFundMetadata.FUND_BUYPRICE_KEY, buyPrice);
		cv.put(PortfolioFundMetadata.FUND_ROUNDLOT_KEY, roundLot);
		cv.put(PortfolioFundMetadata.FUND_CAPITALGAINTAX_KEY, capitalGainTax);
		cv.put(PortfolioFundMetadata.FUND_COUPONTAX_KEY, couponTax);
		database.insert(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, null, cv);
	}
	
	public void addNewShareInTransitionTable(String portfolioName, String shareCODE, String buyDate, float buyPrice, int roundLot, float capitalGainTax, float couponTax)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioShareMetadata.ID, 1);
		cv.put(PortfolioShareMetadata.PORTFOLIO_NAME_KEY, portfolioName);
		cv.put(PortfolioShareMetadata.SHARE_CODE_KEY, shareCODE);
		cv.put(PortfolioShareMetadata.SHARE_BUYDATE_KEY, buyDate);
		cv.put(PortfolioShareMetadata.SHARE_BUYPRICE_KEY, buyPrice);
		cv.put(PortfolioShareMetadata.SHARE_ROUNDLOT_KEY, roundLot);
		cv.put(PortfolioShareMetadata.SHARE_CAPITALGAINTAX_KEY, capitalGainTax);
		cv.put(PortfolioShareMetadata.SHARE_COUPONTAX_KEY, couponTax);
		database.insert(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, null, cv);
	}
	
	//.........
	
	
	
	//--------------------------------SELECT methods----------------------------//
	public Cursor getAllSavedPortfolio()
	{
		return database.query(PortfolioMetaData.PORTFOLIO_TABLE, null, null, null, null, null, null);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//------------------------This 3 Methods returns--------------------------------------------------//
	//------------------------------------------------------------------------------------------------//
	//----[ _id | nomePortafoglio | isin | variazioneAssoluta | variazionePercentuale | prezzo]-------//
	//------------------------------------------------------------------------------------------------//
	//------------------------of the shares in a specific Portfolio-----------------------------------//
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Cursor getAllBondOverviewInPortfolio(String portfolioName)
	{
		return database.query(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE+" as P join "+BondMetaData.BOND_TABLE+" as B", 
				new String[] {"P."+PortfolioBondMetadata.ID, "P."+PortfolioBondMetadata.PORTFOLIO_NAME_KEY, "P."+PortfolioBondMetadata.BOND_ISIN_KEY+" as 'isin'", 
				"B."+BondMetaData.BOND_VARIATION_KEY, "B."+BondMetaData.BOND_PERCVAR_KEY, "B."+BondMetaData.BOND_LASTCONTRACTPRICE_KEY+" as 'prezzo'"}, 
				"P."+PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioBondMetadata.BOND_ISIN_KEY+" = B.'"+BondMetaData.BOND_ISIN+"'", 
				null, null, null, null);
	}
	
	public Cursor getAllFundOverviewInPortfolio(String portfolioName)
	{
		return database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE+" as P join "+FundMetaData.FUND_TABLE+" as F", 
				new String[] {"P."+PortfolioFundMetadata.ID, "P."+PortfolioFundMetadata.PORTFOLIO_NAME_KEY, "P."+PortfolioFundMetadata.FUND_ISIN_KEY+" as 'isin'", 
				"F."+FundMetaData.FUND_VARIATION_KEY, "F."+FundMetaData.FUND_PERCVAR_KEY, "F."+FundMetaData.FUND_LASTPRIZE_KEY+" as 'prezzo'"}, 
				"P."+PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioFundMetadata.FUND_ISIN_KEY+" = F.'"+FundMetaData.FUND_ISIN+"'", 
				null, null, null, null);
	}
	
	public Cursor getAllShareOverviewInPortfolio(String portfolioName)
	{
		return database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE+" as P join "+ShareMetaData.SHARE_TABLE+" as S", 
				new String[] {"P."+PortfolioShareMetadata.ID, "P."+PortfolioShareMetadata.PORTFOLIO_NAME_KEY, "P."+PortfolioShareMetadata.SHARE_CODE_KEY+" as 'isin'", 
				"S."+ShareMetaData.SHARE_VARIATION_KEY, "S."+ShareMetaData.SHARE_PERCVAR_KEY, "S."+ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY+" as 'prezzo'"}, 
				"P."+PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioShareMetadata.SHARE_CODE_KEY+" = S.'"+ShareMetaData.SHARE_CODE+"'", 
				null, null, null, null);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//------------------------This 3 Methods returns all details -------------------------------------//
	//------------------------of a shares in a specific Portfolio-------------------------------------//
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Cursor getBondDetail(String portfolioName, String ISIN)
	{
		return database.query(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE+" as P join "+BondMetaData.BOND_TABLE+" as S", 
				null,"P."+PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioBondMetadata.BOND_ISIN_KEY+" = S.'"+BondMetaData.BOND_ISIN+"' = '"+ISIN+"'", 
				null, null, null, null);
	}
	
	public Cursor getFondDetail(String portfolioName, String ISIN)
	{
		return database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE+" as P join "+FundMetaData.FUND_TABLE+" as S", 
				null,"P."+PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioFundMetadata.FUND_ISIN_KEY+" = S.'"+FundMetaData.FUND_ISIN+"' = '"+ISIN+"'", 
				null, null, null, null);
	}
	
	public Cursor getShareDetail(String portfolioName, String CODE)
	{
		return database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE+" as P join "+ShareMetaData.SHARE_TABLE+" as S", 
				null,"P."+PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioShareMetadata.SHARE_CODE_KEY+" = S.'"+ShareMetaData.SHARE_CODE+"' = '"+CODE+"'", 
				null, null, null, null);
	}
	
	
	//--------------------------------UPDATE methods----------------------------//
	
	public void updateSelectedPortfolio(String previousName, String newName, String newDescription, String newDate)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioMetaData.PORTFOLIO_NAME_KEY, newName);
		cv.put(PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY, newDescription);
		cv.put(PortfolioMetaData.PORTFOLIO_CREATION_DATE_KEY, newDate);
		database.update(PortfolioMetaData.PORTFOLIO_TABLE, cv, PortfolioMetaData.PORTFOLIO_NAME_KEY+" = '"+previousName+"'", null);
	}
	
	public void updateSelectedBond(int _id, String ISIN, String name, String currency, String market, String marketPhase, float lastContractPrice, 
			float percVariation, float variation, String lastContractDate, int lastVolume, int buyVolume, int sellVolume, 
			int totalVolume, float buyPrice, float sellPrice, float maxToday, float minToday, float maxYear, float minYear, 
			String maxYearDate, String minYearDate, float lastClose, String expirationDate, String couponDate, float coupon, 
			int minRoundLot, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(BondMetaData.ID, _id); //posso ometterlo o no?
		cv.put(BondMetaData.BOND_ISIN, ISIN); //posso ometterlo o no?
		cv.put(BondMetaData.BOND_NAME_KEY, name); //posso ometterlo o no?
		cv.put(BondMetaData.BOND_CURRENCY_KEY, currency);
		cv.put(BondMetaData.BOND_MARKET_KEY, market);
		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, marketPhase);
		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, lastContractPrice);
		cv.put(BondMetaData.BOND_PERCVAR_KEY, percVariation);
		cv.put(BondMetaData.BOND_VARIATION_KEY, variation);
		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, lastContractDate);
		cv.put(BondMetaData.BOND_LASTVOLUME_KEY, lastVolume);
		cv.put(BondMetaData.BOND_BUYVOLUME_KEY, buyVolume);
		cv.put(BondMetaData.BOND_SELLVOLUME_KEY, sellVolume);
		cv.put(BondMetaData.BOND_TOTALVOLUME_KEY, totalVolume);
		cv.put(BondMetaData.BOND_BUYPRICE_KEY, buyPrice);
		cv.put(BondMetaData.BOND_SELLPRICE_KEY, sellPrice);
		cv.put(BondMetaData.BOND_MAXTODAY_KEY, maxToday);
		cv.put(BondMetaData.BOND_MINTODAY_KEY, minToday);
		cv.put(BondMetaData.BOND_MAXYEAR_KEY, maxYear);
		cv.put(BondMetaData.BOND_MINYEAR_KEY, minYear);
		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, maxYearDate);
		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, minYearDate);
		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, lastClose);
		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, expirationDate);
		cv.put(BondMetaData.BOND_COUPONDATE_KEY, couponDate);
		cv.put(BondMetaData.BOND_COUPON_KEY, coupon);
		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, minRoundLot);
		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
		
		database.update(BondMetaData.BOND_TABLE, cv, BondMetaData.BOND_ISIN+" = '"+ISIN+"'", null);
	}
	
	public void updateSelectedBondByQuotationObject(Quotation_Bond newBond, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(BondMetaData.ID, 1); // ometto?
		cv.put(BondMetaData.BOND_ISIN, newBond.getISIN()); //ometto?
		cv.put(BondMetaData.BOND_NAME_KEY, newBond.getName()); //ometto?
		cv.put(BondMetaData.BOND_CURRENCY_KEY, newBond.getValuta());
		cv.put(BondMetaData.BOND_MARKET_KEY, newBond.getMercato());
		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, newBond.getFaseMercato());
		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, newBond.getPrezzoUltimoContratto());
		cv.put(BondMetaData.BOND_PERCVAR_KEY, newBond.getVariazionePercentuale());
		cv.put(BondMetaData.BOND_VARIATION_KEY, newBond.getVariazioneAssoluta());
		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, newBond.getDataUltimoContratto().toGMTString());
		cv.put(BondMetaData.BOND_LASTVOLUME_KEY, newBond.getVolumeUltimo());
		cv.put(BondMetaData.BOND_BUYVOLUME_KEY, newBond.getVolumeAcquisto());
		cv.put(BondMetaData.BOND_SELLVOLUME_KEY, newBond.getVolumeVendita());
		cv.put(BondMetaData.BOND_TOTALVOLUME_KEY, newBond.getVolumeTotale());
		cv.put(BondMetaData.BOND_BUYPRICE_KEY, newBond.getPrezzoAcquisto());
		cv.put(BondMetaData.BOND_SELLPRICE_KEY, newBond.getPrezzoVendita());
		cv.put(BondMetaData.BOND_MAXTODAY_KEY, newBond.getMaxOggi());
		cv.put(BondMetaData.BOND_MINTODAY_KEY, newBond.getMinOggi());
		cv.put(BondMetaData.BOND_MAXYEAR_KEY, newBond.getMaxAnno());
		cv.put(BondMetaData.BOND_MINYEAR_KEY, newBond.getMinAnno());
		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, newBond.getDataMaxAnno().toGMTString());
		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, newBond.getDataMinAnno().toGMTString());
		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, newBond.getAperturaChiusuraPrecedente());
		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, newBond.getScadenza().toGMTString());
		cv.put(BondMetaData.BOND_COUPONDATE_KEY, newBond.getDataStaccoCedola().toGMTString());
		cv.put(BondMetaData.BOND_COUPON_KEY, newBond.getCedola());
		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, newBond.getLottoMinimo());
		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
		
		database.update(BondMetaData.BOND_TABLE, cv, BondMetaData.BOND_ISIN+" = '"+newBond.getISIN()+"'", null);
	}
	
	public void updateSelectedFund(int _id, String ISIN, String name, String manager, String category, String benchmark, 
			float lastPrize, String lastPrizeDate, float precPrize, String currency, float percVariation, float variation, 
			float performance1Month, float performance3Month, float performance1Year, float performance3Year, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(FundMetaData.ID, _id);// ometto?
		cv.put(FundMetaData.FUND_ISIN, ISIN);// ometto?
		cv.put(FundMetaData.FUND_NAME_KEY, name);// ometto?
		cv.put(FundMetaData.FUND_MANAGER_KEY, manager);
		cv.put(FundMetaData.FUND_CATEGORY_KEY, category);
		cv.put(FundMetaData.FUND_BENCHMARK_KEY, benchmark);
		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, lastPrize);
		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, lastPrizeDate);
		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, precPrize);
		cv.put(FundMetaData.FUND_CURRENCY_KEY, currency);
		cv.put(FundMetaData.FUND_PERCVAR_KEY, percVariation);
		cv.put(FundMetaData.FUND_VARIATION_KEY, variation);
		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, performance1Month);
		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, performance3Month);
		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, performance1Year);
		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, performance3Year);
		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
		
		database.update(FundMetaData.FUND_TABLE, cv, FundMetaData.FUND_ISIN+" = '"+ISIN+"'", null);

	}
	
	public void updateSelectedFundByQuotationObject(Quotation_Fund newFund, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(FundMetaData.ID, 1); // ometto?
		cv.put(FundMetaData.FUND_ISIN, newFund.getISIN()); // ometto?
		cv.put(FundMetaData.FUND_NAME_KEY, newFund.getName()); // ometto?
		cv.put(FundMetaData.FUND_MANAGER_KEY, newFund.getNomeGestore());
		cv.put(FundMetaData.FUND_CATEGORY_KEY, newFund.getCategoriaAssociati());
		cv.put(FundMetaData.FUND_BENCHMARK_KEY, newFund.getBenchmarkDichiarato());
		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, newFund.getUltimoPrezzo());
		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, newFund.getDataUltimoPrezzo().toGMTString());
		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, newFund.getPrezzoPrecedente());
		cv.put(FundMetaData.FUND_CURRENCY_KEY, newFund.getValuta());
		cv.put(FundMetaData.FUND_PERCVAR_KEY, newFund.getVariazionePercentuale());
		cv.put(FundMetaData.FUND_VARIATION_KEY, newFund.getVariazioneAssoluta());
		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, newFund.getPerformance1Mese());
		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, newFund.getPerformance3Mesi());
		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, newFund.getPerformance1Anno());
		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, newFund.getPerformance3Anni());
		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
		
		database.update(FundMetaData.FUND_TABLE, cv, FundMetaData.FUND_ISIN+" = '"+newFund.getISIN()+"'", null);
	}
	
	public void updateSelectedShare(int _id, String CODE, String name, int minRoundLot, String marketPhase, float lastContractPrice, 
			float percVariation, float variation, String lastContractDate, float buyPrice, float sellPrice, int lastAmount, 
			int buyAmount, int sellAmount, int totalAmount, float maxToday, float minToday, float maxYear, float minYear, 
			String maxYearDate, String minYearDate, float lastClose, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.ID, _id); //ometto?
		cv.put(ShareMetaData.SHARE_CODE, CODE); //ometto?
		cv.put(ShareMetaData.SHARE_NAME_KEY, name); //ometto?
		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, minRoundLot);
		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, marketPhase);
		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, lastContractPrice);
		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, percVariation);
		cv.put(ShareMetaData.SHARE_VARIATION_KEY, variation);
		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, lastContractDate);
		cv.put(ShareMetaData.SHARE_BUYPRICE_KEY, buyPrice);
		cv.put(ShareMetaData.SHARE_SELLPRICE_KEY, sellPrice);
		cv.put(ShareMetaData.SHARE_LASTAMOUNT_KEY, lastAmount);
		cv.put(ShareMetaData.SHARE_BUYAMOUNT_KEY, buyAmount);
		cv.put(ShareMetaData.SHARE_SELLAMOUNT_KEY, sellAmount);
		cv.put(ShareMetaData.SHARE_TOTALAMOUNT_KEY, totalAmount);
		cv.put(ShareMetaData.SHARE_MAXTODAY_KEY, maxToday);
		cv.put(ShareMetaData.SHARE_MINTODAY_KEY, minToday);
		cv.put(ShareMetaData.SHARE_MAXYEAR_KEY, maxYear);
		cv.put(ShareMetaData.SHARE_MINYEAR_KEY, minYear);
		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, maxYearDate);
		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, minYearDate);
		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, lastClose);
		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
		
		database.update(ShareMetaData.SHARE_TABLE, cv, ShareMetaData.SHARE_CODE+" = '"+CODE+"'", null);
	}
	
	public void updateSelectedShareByQuotationObject(Quotation_Share newShare, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.ID, 1);
		cv.put(ShareMetaData.SHARE_CODE, newShare.getISIN());
		cv.put(ShareMetaData.SHARE_NAME_KEY, newShare.getName());
		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, newShare.getLottoMinimo());
		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, newShare.getFaseMercato());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, newShare.getPrezzoUltimoContratto());
		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, newShare.getVariazionePercentuale());
		cv.put(ShareMetaData.SHARE_VARIATION_KEY, newShare.getVariazioneAssoluta());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, newShare.getDataOraUltimoAcquisto().toGMTString());
		cv.put(ShareMetaData.SHARE_BUYPRICE_KEY, newShare.getPrezzoAcquisto());
		cv.put(ShareMetaData.SHARE_SELLPRICE_KEY, newShare.getPrezzoVendita());
		cv.put(ShareMetaData.SHARE_LASTAMOUNT_KEY, newShare.getQuantitaUltimo());
		cv.put(ShareMetaData.SHARE_BUYAMOUNT_KEY, newShare.getQuantitaAcquisto());
		cv.put(ShareMetaData.SHARE_SELLAMOUNT_KEY, newShare.getQuantitaVendita());
		cv.put(ShareMetaData.SHARE_TOTALAMOUNT_KEY, newShare.getQuantitaTotale());
		cv.put(ShareMetaData.SHARE_MAXTODAY_KEY, newShare.getMaxOggi());
		cv.put(ShareMetaData.SHARE_MINTODAY_KEY, newShare.getMinOggi());
		cv.put(ShareMetaData.SHARE_MAXYEAR_KEY, newShare.getMaxAnno());
		cv.put(ShareMetaData.SHARE_MINYEAR_KEY, newShare.getMinAnno());
		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, newShare.getDataMaxAnno().toGMTString());
		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, newShare.getDataMinAnno().toGMTString());
		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, newShare.getChiusuraPrecedente());
		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
		
		database.update(ShareMetaData.SHARE_TABLE, cv, ShareMetaData.SHARE_CODE+" = '"+newShare.getISIN()+"'", null);
	}
	
	
	//--------------------------------DELETE methods----------------------------//
	
	public void deletePortfolioByName(String name)
	{
		database.delete(PortfolioMetaData.PORTFOLIO_TABLE, PortfolioMetaData.PORTFOLIO_NAME_KEY+" = '"+name+"'", null);
	}
	
	public void deleteBond(String ISIN)
	{
		database.delete(BondMetaData.BOND_TABLE, BondMetaData.BOND_ISIN+" = '"+ISIN+"'", null);
	}
	
	public void deleteFund(String ISIN)
	{
		database.delete(FundMetaData.FUND_TABLE, FundMetaData.FUND_ISIN+" = '"+ISIN+"'", null);
	}
	
	public void deleteShare(String CODE)
	{
		database.delete(ShareMetaData.SHARE_TABLE, ShareMetaData.SHARE_CODE+" = '"+CODE+"'", null);
	}
	
	public void deleteBondInTransitionTable(String portfolioName, String ISIN) 
	{
		database.delete(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, PortfolioBondMetadata.BOND_ISIN_KEY+" = '"+ISIN+"' AND '"+PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null);
	}
	
	public void deleteFundInTransitionTable(String portfolioName, String ISIN) 
	{
		database.delete(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, PortfolioFundMetadata.FUND_ISIN_KEY+" = '"+ISIN+"' AND '"+PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null);
	}
	
	public void deleteShareInTransitionTable(String portfolioName, String CODE) 
	{
		database.delete(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, PortfolioShareMetadata.SHARE_CODE_KEY+" = '"+CODE+"' AND '"+PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null);
	}
	
}
