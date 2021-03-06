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
//		database.execSQL("DROP TRIGGER deleteBond");
//		database.execSQL("DROP TRIGGER deleteFund");
//		database.execSQL("DROP TRIGGER deleteShare");
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
		static final String PORTFOLIO_LASTUPDATE_KEY = "ultimoAggiornamento";
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
		static final String BOND_SOURCESITE_KEY = "sitoSorgente";
		static final String BOND_SOURCESITEURL_KEY = "sitoSorgenteUrl";
		static final String BOND_PREFERREDSITE_KEY = "sitoPreferito";
		static final String BOND_IGNOREDSITES_KEY = "sitiIgnorati";
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
		static final String FUND_SOURCESITE_KEY = "sitoSorgente";
		static final String FUND_SOURCESITEURL_KEY = "sitoSorgenteUrl";
		static final String FUND_PREFERREDSITE_KEY = "sitoPreferito";
		static final String FUND_IGNOREDSITES_KEY = "sitiIgnorati";
	}
	
	static class ShareMetaData
	{
		static final String SHARE_TABLE = "Quotation_Share";
		static final String ID = "_id";
		static final String SHARE_CODE = "codice";
		static final String SHARE_ISIN = "isin";
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
		static final String SHARE_SOURCESITE_KEY = "sitoSorgente";
		static final String SHARE_SOURCESITEURL_KEY = "sitoSorgenteUrl";
		static final String SHARE_PREFERREDSITE_KEY = "sitoPreferito";
		static final String SHARE_IGNOREDSITES_KEY = "sitiIgnorati";
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
	}
	
	static class PortfolioShareMetadata
	{
		static final String PORTFOLIO_SHARE_TABLE = "Table_Portfolio_Share";
		static final String ID = "_id";
		static final String PORTFOLIO_NAME_KEY = "nomePortafoglio";
		static final String SHARE_ISIN_KEY = "isinAzione";
		static final String SHARE_BUYDATE_KEY = "dataAcquisto";
		static final String SHARE_BUYPRICE_KEY = "prezzoAcquisto";
		static final String SHARE_ROUNDLOT_KEY = "lotto";
	}
	
	//-----------------------------------METADATA tabella Siti/Tipologia-------------------------------//
	static class SiteTypeMetadata
	{
		static final String SITE_TYPE_TABLE = "Table_Site_Type";
		static final String ID = "_id";
		static final String VERSION = "versione";
		static final String TYPE = "tipologia";
		static final String SITE = "sito";
	}
	
	//-----------------------------------METADATA tabella dati storici---------------------------------//
	static class HistoricalDataMetadata
	{
		static final String HISTORICAL_DATA_TABLE = "Table_Historical_Data";
		static final String ID = "_id";
		static final String ISIN = "isin";
		static final String DATE = "data";
		static final String VALUE = "valore";
	}
	
	
	//-------------------------------------STRING per creazione tabelle-------------------------------//
	private static final String TABLE_PORTFOLIO_CREATE = "CREATE TABLE "+PortfolioMetaData.PORTFOLIO_TABLE+ " (" +
			PortfolioMetaData.ID +" INTEGER NOT NULL, " +
			PortfolioMetaData.PORTFOLIO_NAME_KEY +" TEXT PRIMARY KEY, " +
			PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY +" TEXT NOT NULL, " +
			PortfolioMetaData.PORTFOLIO_CREATION_DATE_KEY +" TEXT NOT NULL, " +
			PortfolioMetaData.PORTFOLIO_LASTUPDATE_KEY +" TEXT NOT NULL);";
	
	private static final String TABLE_BOND_CREATE = "CREATE TABLE "+BondMetaData.BOND_TABLE+" (" +
			BondMetaData.ID +" INTEGER NOT NULL, " +
			BondMetaData.BOND_ISIN +" TEXT PRIMARY KEY, " +
			BondMetaData.BOND_NAME_KEY +" TEXT, " +
			BondMetaData.BOND_CURRENCY_KEY +" TEXT, " +
			BondMetaData.BOND_MARKET_KEY +" TEXT, " +
			BondMetaData.BOND_MARKETPHASE_KEY +" TEXT, " +
			BondMetaData.BOND_LASTCONTRACTPRICE_KEY +" REAL, " +
			BondMetaData.BOND_PERCVAR_KEY +" REAL, " +
			BondMetaData.BOND_VARIATION_KEY +" REAL, " +
			BondMetaData.BOND_LASTCONTRACTDATE_KEY +" TEXT, " +
			BondMetaData.BOND_LASTVOLUME_KEY +" INTEGER, " +
			BondMetaData.BOND_BUYVOLUME_KEY +" INTEGER, " +
			BondMetaData.BOND_SELLVOLUME_KEY +" INTEGER, " +
			BondMetaData.BOND_TOTALVOLUME_KEY +" INTEGER, " +
			BondMetaData.BOND_BUYPRICE_KEY +" REAL, " +
			BondMetaData.BOND_SELLPRICE_KEY +" REAL, " +
			BondMetaData.BOND_MAXTODAY_KEY +" REAL, " +
			BondMetaData.BOND_MINTODAY_KEY +" REAL, " +
			BondMetaData.BOND_MAXYEAR_KEY +" REAL, " +
			BondMetaData.BOND_MINYEAR_KEY +" REAL, " +
			BondMetaData.BOND_MAXYEARDATE_KEY +" TEXT, " +
			BondMetaData.BOND_MINYEARDATE_KEY +" TEXT, " +
			BondMetaData.BOND_LASTCLOSE_KEY +" REAL, " +
			BondMetaData.BOND_EXPIRATIONDATE_KEY +" TEXT, " +
			BondMetaData.BOND_COUPONDATE_KEY +" TEXT, " +
			BondMetaData.BOND_COUPON_KEY +" REAL, " +
			BondMetaData.BOND_MINROUNDLOT_KEY +" INTEGER, " +
			BondMetaData.BOND_LASTUPDATE_KEY +" TEXT, " +
			BondMetaData.BOND_SOURCESITE_KEY +" TEXT, " +
			BondMetaData.BOND_SOURCESITEURL_KEY +" TEXT, " +
			BondMetaData.BOND_PREFERREDSITE_KEY +" TEXT, " +
			BondMetaData.BOND_IGNOREDSITES_KEY +" TEXT);";
	
	private static final String TABLE_FUND_CREATE = "CREATE TABLE "+FundMetaData.FUND_TABLE+" (" +
			FundMetaData.ID +" INTEGER NOT NULL, " +
			FundMetaData.FUND_ISIN +" TEXT, " +
			FundMetaData.FUND_NAME_KEY +" TEXT, " +
			FundMetaData.FUND_MANAGER_KEY +" TEXT, " +
			FundMetaData.FUND_CATEGORY_KEY +" TEXT, " +
			FundMetaData.FUND_BENCHMARK_KEY +" TEXT, " +
			FundMetaData.FUND_LASTPRIZE_KEY +" REAL, " +
			FundMetaData.FUND_LASTPRIZEDATE_KEY +" TEXT, " +
			FundMetaData.FUND_PRECPRIZE_KEY +" REAL, " +
			FundMetaData.FUND_CURRENCY_KEY +" TEXT, " +
			FundMetaData.FUND_PERCVAR_KEY +" REAL, " +
			FundMetaData.FUND_VARIATION_KEY +" REAL, " +
			FundMetaData.FUND_PERFORMANCE1MONTH +" REAL, " +
			FundMetaData.FUND_PERFORMANCE3MONTH +" REAL, " +
			FundMetaData.FUND_PERFORMANCE1YEAR +" REAL, " +
			FundMetaData.FUND_PERFORMANCE3YEAR +" REAL, " +
			FundMetaData.FUND_LASTUPDATE_KEY +" TEXT, " +
			FundMetaData.FUND_SOURCESITE_KEY +" TEXT, " +
			FundMetaData.FUND_SOURCESITEURL_KEY +" TEXT, " +
			FundMetaData.FUND_PREFERREDSITE_KEY +" TEXT, " +
			FundMetaData.FUND_IGNOREDSITES_KEY +" TEXT);";
	
	private static final String TABLE_SHARE_CREATE = "CREATE TABLE "+ShareMetaData.SHARE_TABLE+" (" +
			ShareMetaData.ID +" INTEGER NOT NULL, " +
			ShareMetaData.SHARE_CODE +" TEXT, " +
			ShareMetaData.SHARE_ISIN +" TEXT PRIMARY KEY, " +
			ShareMetaData.SHARE_NAME_KEY +" TEXT, " +
			ShareMetaData.SHARE_MINROUNDLOT_KEY +" INTEGER, " +
			ShareMetaData.SHARE_MARKETPHASE_KEY +" TEXT, " +
			ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY +" REAL, " +
			ShareMetaData.SHARE_PERCVAR_KEY +" REAL, " +
			ShareMetaData.SHARE_VARIATION_KEY +" REAL, " +
			ShareMetaData.SHARE_LASTCONTRACTDATE_KEY +" TEXT, " +
			ShareMetaData.SHARE_BUYPRICE_KEY +" REAL, " +
			ShareMetaData.SHARE_SELLPRICE_KEY +" REAL, " +
			ShareMetaData.SHARE_LASTAMOUNT_KEY +" INTEGER, " +
			ShareMetaData.SHARE_BUYAMOUNT_KEY +" INTEGER, " +
			ShareMetaData.SHARE_SELLAMOUNT_KEY +" INTEGER, " +
			ShareMetaData.SHARE_TOTALAMOUNT_KEY +" INTEGER, " +
			ShareMetaData.SHARE_MAXTODAY_KEY +" REAL, " +
			ShareMetaData.SHARE_MINTODAY_KEY +" REAL, " +
			ShareMetaData.SHARE_MAXYEAR_KEY +" REAL, " +
			ShareMetaData.SHARE_MINYEAR_KEY +" REAL, " +
			ShareMetaData.SHARE_MAXYEARDATE_KEY +" TEXT, " +
			ShareMetaData.SHARE_MINYEARDATE_KEY +" TEXT, " +
			ShareMetaData.SHARE_LASTCLOSE_KEY +" REAL, " +
			ShareMetaData.SHARE_LASTUPDATE_KEY +" TEXT, " +
			ShareMetaData.SHARE_SOURCESITE_KEY +" TEXT, " +
			ShareMetaData.SHARE_SOURCESITEURL_KEY +" TEXT, " +
			ShareMetaData.SHARE_PREFERREDSITE_KEY +" TEXT, " +
			ShareMetaData.SHARE_IGNOREDSITES_KEY +" TEXT);";
	
	//-------------------------------------STRING per creazione tabelle di transizione-------------------//
	private static final String TABLE_PORTFOLIO_BOND_CREATE = "CREATE TABLE "+PortfolioBondMetadata.PORTFOLIO_BOND_TABLE+" (" +
			PortfolioBondMetadata.ID +" INTEGER NOT NULL, " +
			PortfolioBondMetadata.PORTFOLIO_NAME_KEY +" TEXT NOT NULL, " +
			PortfolioBondMetadata.BOND_ISIN_KEY +" TEXT NOT NULL, " +
			PortfolioBondMetadata.BOND_BUYDATE_KEY +" TEXT, " +
			PortfolioBondMetadata.BOND_BUYPRICE_KEY +" REAL, " +
			PortfolioBondMetadata.BOND_ROUNDLOT_KEY +" INTEGER, " +
			"PRIMARY KEY (" +PortfolioBondMetadata.PORTFOLIO_NAME_KEY+", " +PortfolioBondMetadata.BOND_ISIN_KEY+", " +PortfolioBondMetadata.BOND_BUYDATE_KEY+"), " +
			"FOREIGN KEY (" +PortfolioBondMetadata.PORTFOLIO_NAME_KEY+") REFERENCES "+PortfolioMetaData.PORTFOLIO_TABLE+"("+PortfolioMetaData.PORTFOLIO_NAME_KEY+")" +
			"FOREIGN KEY (" +PortfolioBondMetadata.BOND_ISIN_KEY+") REFERENCES "+BondMetaData.BOND_TABLE+"("+BondMetaData.BOND_ISIN+"));";
	
	private static final String TABLE_PORTFOLIO_FUND_CREATE = "CREATE TABLE "+PortfolioFundMetadata.PORTFOLIO_FUND_TABLE+" (" +
			PortfolioFundMetadata.ID +" INTEGER NOT NULL, " +
			PortfolioFundMetadata.PORTFOLIO_NAME_KEY +" TEXT NOT NULL, " +
			PortfolioFundMetadata.FUND_ISIN_KEY +" TEXT NOT NULL, " +
			PortfolioFundMetadata.FUND_BUYDATE_KEY +" TEXT, " +
			PortfolioFundMetadata.FUND_BUYPRICE_KEY +" REAL, " +
			PortfolioFundMetadata.FUND_ROUNDLOT_KEY +" INTEGER, " +
			"PRIMARY KEY (" +PortfolioFundMetadata.PORTFOLIO_NAME_KEY+", " +PortfolioFundMetadata.FUND_ISIN_KEY+", " +PortfolioFundMetadata.FUND_BUYDATE_KEY+"), " +
			"FOREIGN KEY (" +PortfolioFundMetadata.PORTFOLIO_NAME_KEY+") REFERENCES "+PortfolioMetaData.PORTFOLIO_TABLE+"("+PortfolioMetaData.PORTFOLIO_NAME_KEY+")" +
			"FOREIGN KEY (" +PortfolioFundMetadata.FUND_ISIN_KEY+") REFERENCES "+FundMetaData.FUND_TABLE+"("+FundMetaData.FUND_ISIN+"));";
	
	private static final String TABLE_PORTFOLIO_SHARE_CREATE = "CREATE TABLE "+PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE+" (" +
			PortfolioShareMetadata.ID +" INTEGER NOT NULL, " +
			PortfolioShareMetadata.PORTFOLIO_NAME_KEY +" TEXT NOT NULL, " +
			PortfolioShareMetadata.SHARE_ISIN_KEY +" TEXT NOT NULL, " +
			PortfolioShareMetadata.SHARE_BUYDATE_KEY +" TEXT, " +
			PortfolioShareMetadata.SHARE_BUYPRICE_KEY +" REAL, " +
			PortfolioShareMetadata.SHARE_ROUNDLOT_KEY +" INTEGER, " +
			"PRIMARY KEY (" +PortfolioShareMetadata.PORTFOLIO_NAME_KEY+", " +PortfolioShareMetadata.SHARE_ISIN_KEY+", " +PortfolioShareMetadata.SHARE_BUYDATE_KEY+"), " +
			"FOREIGN KEY (" +PortfolioShareMetadata.PORTFOLIO_NAME_KEY+") REFERENCES "+PortfolioMetaData.PORTFOLIO_TABLE+"("+PortfolioMetaData.PORTFOLIO_NAME_KEY+")" +
			"FOREIGN KEY (" +PortfolioShareMetadata.SHARE_ISIN_KEY+") REFERENCES "+ShareMetaData.SHARE_TABLE+"("+ShareMetaData.SHARE_ISIN+"));";
	
	//-------------------------------------STRING per creazione tabella Siti/Type-------------------//
	private static final String TABLE_SITE_TYPE_CREATE = "CREATE TABLE "+SiteTypeMetadata.SITE_TYPE_TABLE+" (" +
			SiteTypeMetadata.ID +" INTEGER NOT NULL, " +
			SiteTypeMetadata.VERSION +" INTEGER NOT NULL, " +
			SiteTypeMetadata.TYPE +" TEXT NOT NULL, " +
			SiteTypeMetadata.SITE +" TEXT NOT NULL, " +
			"PRIMARY KEY (" +SiteTypeMetadata.VERSION+", " +SiteTypeMetadata.TYPE+", " +SiteTypeMetadata.SITE+"));";
	
	//-------------------------------------STRING per creazione tabella dati storici-------------------//
		private static final String TABLE_HISTORICAL_DATA_CREATE = "CREATE TABLE "+HistoricalDataMetadata.HISTORICAL_DATA_TABLE+" (" +
				HistoricalDataMetadata.ID +" INTEGER NOT NULL, " +
				HistoricalDataMetadata.ISIN +" TEXT NOT NULL, " +
				HistoricalDataMetadata.DATE +" TEXT NOT NULL, " +
				HistoricalDataMetadata.VALUE +" TEXT NOT NULL, " +
				"PRIMARY KEY (" +HistoricalDataMetadata.ISIN+", " +HistoricalDataMetadata.DATE+"));";
	
	//-------------------------------------HELPER class---------------------------------------//
	private class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context, String name, CursorFactory factory, int version)
		{
			super(context, name, factory, version);
		}
		
		public void onCreate(SQLiteDatabase _db) 
		{ 
			//per ogni tabella nel database facciamo una execSQL con la relativa stringa di ceazione
            _db.execSQL(TABLE_PORTFOLIO_CREATE);
            _db.execSQL(TABLE_BOND_CREATE);            
            _db.execSQL(TABLE_FUND_CREATE);
            _db.execSQL(TABLE_SHARE_CREATE);
            _db.execSQL(TABLE_PORTFOLIO_BOND_CREATE);
            _db.execSQL(TABLE_PORTFOLIO_FUND_CREATE);
            _db.execSQL(TABLE_PORTFOLIO_SHARE_CREATE);
            _db.execSQL(TABLE_SITE_TYPE_CREATE);
            _db.execSQL(TABLE_HISTORICAL_DATA_CREATE);
            
/*            _db.execSQL("CREATE TRIGGER deleteBond"+ 
					" BEFORE DELETE ON "+PortfolioBondMetadata.PORTFOLIO_BOND_TABLE+
					" FOR EACH ROW"+
					" WHEN OLD."+PortfolioBondMetadata.BOND_ISIN_KEY+" NOT IN (SELECT "+PortfolioBondMetadata.BOND_ISIN_KEY+" FROM "+PortfolioBondMetadata.PORTFOLIO_BOND_TABLE+")"+
					" BEGIN"+
					" DELETE FROM "+BondMetaData.BOND_TABLE+" WHERE "+PortfolioBondMetadata.BOND_ISIN_KEY+" = OLD."+PortfolioBondMetadata.BOND_ISIN_KEY+";"+
        			" END;");
            _db.execSQL("CREATE TRIGGER deleteFund"+ 
					" AFTER DELETE ON "+PortfolioFundMetadata.PORTFOLIO_FUND_TABLE+
					" FOR EACH ROW"+
					" WHEN OLD."+PortfolioFundMetadata.FUND_ISIN_KEY+" NOT IN (SELECT "+PortfolioFundMetadata.FUND_ISIN_KEY+" FROM "+PortfolioFundMetadata.PORTFOLIO_FUND_TABLE+")"+
					" BEGIN"+
					" DELETE FROM "+FundMetaData.FUND_TABLE+" WHERE "+PortfolioFundMetadata.FUND_ISIN_KEY+" = OLD."+PortfolioFundMetadata.FUND_ISIN_KEY+";"+
					" END;");
            _db.execSQL("CREATE TRIGGER deleteShare"+ 
					" AFTER DELETE ON "+PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE+
					" FOR EACH ROW"+
					" WHEN OLD."+PortfolioShareMetadata.SHARE_CODE_KEY+" NOT IN (SELECT "+PortfolioShareMetadata.SHARE_CODE_KEY+" FROM "+PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE+")"+
					" BEGIN"+
					" DELETE FROM "+ShareMetaData.SHARE_TABLE+" WHERE "+PortfolioShareMetadata.SHARE_CODE_KEY+" = OLD."+PortfolioShareMetadata.SHARE_CODE_KEY+";"+
					" END;");
*/
            
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
	public void addNewPortfolio(int _id, String name, String description, String creationDate, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioMetaData.ID, _id);
		cv.put(PortfolioMetaData.PORTFOLIO_NAME_KEY, name);
		cv.put(PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY, description);
		cv.put(PortfolioMetaData.PORTFOLIO_CREATION_DATE_KEY, creationDate);
		cv.put(PortfolioMetaData.PORTFOLIO_LASTUPDATE_KEY, lastUpdate);
		database.insert(PortfolioMetaData.PORTFOLIO_TABLE, null, cv);
	}
	
//	public void addNewBond(int _id, String isin, String name, String currency, String market, String marketPhase, float lastContractPrice, 
//			float percVariation, float variation, String lastContractDate, int lastVolume, int buyVolume, int sellVolume, 
//			int totalVolume, float buyPrice, float sellPrice, float maxToday, float minToday, float maxYear, float minYear, 
//			String maxYearDate, String minYearDate, float lastClose, String expirationDate, String couponDate, float coupon, 
//			int minRoundLot, String lastUpdate, String sourceSite, String preferredSite, String ignoredSites)
//	{
//		ContentValues cv = new ContentValues();
//		cv.put(BondMetaData.ID, _id);
//		cv.put(BondMetaData.BOND_ISIN, isin);
//		cv.put(BondMetaData.BOND_NAME_KEY, name);
//		cv.put(BondMetaData.BOND_CURRENCY_KEY, currency);
//		cv.put(BondMetaData.BOND_MARKET_KEY, market);
//		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, marketPhase);
//		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, lastContractPrice);
//		cv.put(BondMetaData.BOND_PERCVAR_KEY, percVariation);
//		cv.put(BondMetaData.BOND_VARIATION_KEY, variation);
//		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, lastContractDate);
//		cv.put(BondMetaData.BOND_LASTVOLUME_KEY, lastVolume);
//		cv.put(BondMetaData.BOND_BUYVOLUME_KEY, buyVolume);
//		cv.put(BondMetaData.BOND_SELLVOLUME_KEY, sellVolume);
//		cv.put(BondMetaData.BOND_TOTALVOLUME_KEY, totalVolume);
//		cv.put(BondMetaData.BOND_BUYPRICE_KEY, buyPrice);
//		cv.put(BondMetaData.BOND_SELLPRICE_KEY, sellPrice);
//		cv.put(BondMetaData.BOND_MAXTODAY_KEY, maxToday);
//		cv.put(BondMetaData.BOND_MINTODAY_KEY, minToday);
//		cv.put(BondMetaData.BOND_MAXYEAR_KEY, maxYear);
//		cv.put(BondMetaData.BOND_MINYEAR_KEY, minYear);
//		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, maxYearDate);
//		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, minYearDate);
//		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, lastClose);
//		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, expirationDate);
//		cv.put(BondMetaData.BOND_COUPONDATE_KEY, couponDate);
//		cv.put(BondMetaData.BOND_COUPON_KEY, coupon);
//		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, minRoundLot);
//		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
//		cv.put(BondMetaData.BOND_SOURCESITE_KEY, sourceSite);
//		cv.put(BondMetaData.BOND_PREFERREDSITE_KEY, preferredSite);
//		cv.put(BondMetaData.BOND_IGNOREDSITES_KEY, ignoredSites);
//		database.insert(BondMetaData.BOND_TABLE, null, cv);
//	}
	
	public void addNewBondByQuotationObject(Quotation_Bond newBond, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(BondMetaData.ID, 1);
		cv.put(BondMetaData.BOND_ISIN, newBond.getISIN());
		cv.put(BondMetaData.BOND_NAME_KEY, newBond.getName());
		cv.put(BondMetaData.BOND_CURRENCY_KEY, newBond.getValuta());
		cv.put(BondMetaData.BOND_MARKET_KEY, newBond.getMercato());
		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, newBond.getFaseMercato());
		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, newBond.getPrezzoUltimoContratto());
		cv.put(BondMetaData.BOND_PERCVAR_KEY, newBond.getVariazionePercentuale());
		cv.put(BondMetaData.BOND_VARIATION_KEY, newBond.getVariazioneAssoluta());
		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, newBond.getDataUltimoContratto());
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
		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, newBond.getDataMaxAnno());
		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, newBond.getDataMinAnno());
		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, newBond.getAperturaChiusuraPrecedente());
		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, newBond.getScadenza());		
		cv.put(BondMetaData.BOND_COUPONDATE_KEY, newBond.getDataStaccoCedola());
		cv.put(BondMetaData.BOND_COUPON_KEY, newBond.getCedola());
		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, newBond.getLottoMinimo());
		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
		cv.put(BondMetaData.BOND_SOURCESITE_KEY, newBond.getSite());
		cv.put(BondMetaData.BOND_SOURCESITEURL_KEY, newBond.getSiteUrl());
		cv.put(BondMetaData.BOND_PREFERREDSITE_KEY, "");
		cv.put(BondMetaData.BOND_IGNOREDSITES_KEY, "");
		database.insert(BondMetaData.BOND_TABLE, null, cv);
		
		ContentValues cv2 = new ContentValues();
		cv2.put(HistoricalDataMetadata.ID, getLastIndexOfHistoricalData()+1);
		cv2.put(HistoricalDataMetadata.ISIN, newBond.getISIN());
		cv2.put(HistoricalDataMetadata.DATE, lastUpdate);
		cv2.put(HistoricalDataMetadata.VALUE, String.valueOf(newBond.getPrezzoUltimoContratto()));
		database.insert(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, cv2);
		
	}
	
//	public void addNewFund(int _id, String isin, String name, String manager, String category, String benchmark, 
//			float lastPrize, String lastPrizeDate, float precPrize, String currency, float percVariation, float variation, 
//			float performance1Month, float performance3Month, float performance1Year, float performance3Year, String lastUpdate, 
//			String sourceSite, String preferredSite, String ignoredSites)
//	{
//		ContentValues cv = new ContentValues();
//		cv.put(FundMetaData.ID, _id);
//		cv.put(FundMetaData.FUND_ISIN, isin);
//		cv.put(FundMetaData.FUND_NAME_KEY, name);
//		cv.put(FundMetaData.FUND_MANAGER_KEY, manager);
//		cv.put(FundMetaData.FUND_CATEGORY_KEY, category);
//		cv.put(FundMetaData.FUND_BENCHMARK_KEY, benchmark);
//		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, lastPrize);
//		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, lastPrizeDate);
//		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, precPrize);
//		cv.put(FundMetaData.FUND_CURRENCY_KEY, currency);
//		cv.put(FundMetaData.FUND_PERCVAR_KEY, percVariation);
//		cv.put(FundMetaData.FUND_VARIATION_KEY, variation);
//		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, performance1Month);
//		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, performance3Month);
//		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, performance1Year);
//		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, performance3Year);
//		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
//		cv.put(FundMetaData.FUND_SOURCESITE_KEY, sourceSite);
//		cv.put(FundMetaData.FUND_PREFERREDSITE_KEY, preferredSite);
//		cv.put(FundMetaData.FUND_IGNOREDSITES_KEY, ignoredSites);
//		database.insert(FundMetaData.FUND_TABLE, null, cv);
//	}
	
	public void addNewFundByQuotationObject(Quotation_Fund newFund, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(FundMetaData.ID, 1);
		cv.put(FundMetaData.FUND_ISIN, newFund.getISIN());
		cv.put(FundMetaData.FUND_NAME_KEY, newFund.getName());
		cv.put(FundMetaData.FUND_MANAGER_KEY, newFund.getNomeGestore());
		cv.put(FundMetaData.FUND_CATEGORY_KEY, newFund.getCategoriaAssociati());
		cv.put(FundMetaData.FUND_BENCHMARK_KEY, newFund.getBenchmarkDichiarato());
		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, newFund.getUltimoPrezzo());
		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, newFund.getDataUltimoPrezzo());
		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, newFund.getPrezzoPrecedente());
		cv.put(FundMetaData.FUND_CURRENCY_KEY, newFund.getValuta());
		cv.put(FundMetaData.FUND_PERCVAR_KEY, newFund.getVariazionePercentuale());
		cv.put(FundMetaData.FUND_VARIATION_KEY, newFund.getVariazioneAssoluta());
		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, newFund.getPerformance1Mese());
		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, newFund.getPerformance3Mesi());
		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, newFund.getPerformance1Anno());
		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, newFund.getPerformance3Anni());
		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
		cv.put(FundMetaData.FUND_SOURCESITE_KEY, newFund.getSite());
		cv.put(FundMetaData.FUND_SOURCESITEURL_KEY, newFund.getSiteUrl());
		cv.put(FundMetaData.FUND_PREFERREDSITE_KEY, "");
		cv.put(FundMetaData.FUND_IGNOREDSITES_KEY, "");
		database.insert(FundMetaData.FUND_TABLE, null, cv);
		
		ContentValues cv2 = new ContentValues();
		cv2.put(HistoricalDataMetadata.ID, getLastIndexOfHistoricalData()+1);
		cv2.put(HistoricalDataMetadata.ISIN, newFund.getISIN());
		cv2.put(HistoricalDataMetadata.DATE, lastUpdate);
		cv2.put(HistoricalDataMetadata.VALUE, String.valueOf(newFund.getUltimoPrezzo()));
		database.insert(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, cv2);
	}
	
//	public void addNewShare(int _id, String code, String isin, String name, int minRoundLot, String marketPhase, float lastContractPrice, 
//			float percVariation, float variation, String lastContractDate, float buyPrice, float sellPrice, int lastAmount, 
//			int buyAmount, int sellAmount, int totalAmount, float maxToday, float minToday, float maxYear, float minYear, 
//			String maxYearDate, String minYearDate, float lastClose, String lastUpdate, 
//			String sourceSite, String preferredSite, String ignoredSites)
//	{
//		ContentValues cv = new ContentValues();
//		cv.put(ShareMetaData.ID, _id);
//		cv.put(ShareMetaData.SHARE_CODE, code);
//		cv.put(ShareMetaData.SHARE_ISIN, isin);
//		cv.put(ShareMetaData.SHARE_NAME_KEY, name);
//		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, minRoundLot);
//		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, marketPhase);
//		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, lastContractPrice);
//		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, percVariation);
//		cv.put(ShareMetaData.SHARE_VARIATION_KEY, variation);
//		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, lastContractDate);
//		cv.put(ShareMetaData.SHARE_BUYPRICE_KEY, buyPrice);
//		cv.put(ShareMetaData.SHARE_SELLPRICE_KEY, sellPrice);
//		cv.put(ShareMetaData.SHARE_LASTAMOUNT_KEY, lastAmount);
//		cv.put(ShareMetaData.SHARE_BUYAMOUNT_KEY, buyAmount);
//		cv.put(ShareMetaData.SHARE_SELLAMOUNT_KEY, sellAmount);
//		cv.put(ShareMetaData.SHARE_TOTALAMOUNT_KEY, totalAmount);
//		cv.put(ShareMetaData.SHARE_MAXTODAY_KEY, maxToday);
//		cv.put(ShareMetaData.SHARE_MINTODAY_KEY, minToday);
//		cv.put(ShareMetaData.SHARE_MAXYEAR_KEY, maxYear);
//		cv.put(ShareMetaData.SHARE_MINYEAR_KEY, minYear);
//		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, maxYearDate);
//		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, minYearDate);
//		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, lastClose);
//		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
//		cv.put(ShareMetaData.SHARE_SOURCESITE_KEY, sourceSite);
//		cv.put(ShareMetaData.SHARE_PREFERREDSITE_KEY, preferredSite);
//		cv.put(ShareMetaData.SHARE_IGNOREDSITES_KEY, ignoredSites);
//		database.insert(ShareMetaData.SHARE_TABLE, null, cv);
//	}
	
	public void addNewShareByQuotationObject(Quotation_Share newShare, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.ID, 1);
		cv.put(ShareMetaData.SHARE_CODE, newShare.getISIN());
		cv.put(ShareMetaData.SHARE_ISIN, newShare.getISIN());
		cv.put(ShareMetaData.SHARE_NAME_KEY, newShare.getName());
		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, newShare.getLottoMinimo());
		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, newShare.getFaseMercato());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, newShare.getPrezzoUltimoContratto());
		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, newShare.getVariazionePercentuale());
		cv.put(ShareMetaData.SHARE_VARIATION_KEY, newShare.getVariazioneAssoluta());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, newShare.getDataOraUltimoAcquisto());
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
		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, newShare.getDataMaxAnno());
		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, newShare.getDataMinAnno());
		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, newShare.getChiusuraPrecedente());
		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
		cv.put(ShareMetaData.SHARE_SOURCESITE_KEY, newShare.getSite());
		cv.put(ShareMetaData.SHARE_SOURCESITEURL_KEY, newShare.getSiteUrl());
		cv.put(ShareMetaData.SHARE_PREFERREDSITE_KEY, "");
		cv.put(ShareMetaData.SHARE_IGNOREDSITES_KEY, "");
		database.insert(ShareMetaData.SHARE_TABLE, null, cv);
	}
	
	public void addNewTemporaryToolInHistoryTable(int ID, String isin, String date, String value)
	{
		ContentValues cv2 = new ContentValues();
		cv2.put(HistoricalDataMetadata.ID, ID);
		cv2.put(HistoricalDataMetadata.ISIN, isin);
		cv2.put(HistoricalDataMetadata.DATE, date);
		cv2.put(HistoricalDataMetadata.VALUE, value);
		database.insert(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, cv2);
	}
	
	public void addNewBondInTransitionTable(String portfolioName, String bondISIN, String buyDate, float buyPrice, int roundLot)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioBondMetadata.ID, 1);
		cv.put(PortfolioBondMetadata.PORTFOLIO_NAME_KEY, portfolioName);
		cv.put(PortfolioBondMetadata.BOND_ISIN_KEY, bondISIN);
		cv.put(PortfolioBondMetadata.BOND_BUYDATE_KEY, buyDate);
		cv.put(PortfolioBondMetadata.BOND_BUYPRICE_KEY, buyPrice);
		cv.put(PortfolioBondMetadata.BOND_ROUNDLOT_KEY, roundLot);
		database.insert(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, null, cv);
	}
	
	public void addNewFundInTransitionTable(String portfolioName, String fundISIN, String buyDate, float buyPrice, int roundLot)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioFundMetadata.ID, 1);
		cv.put(PortfolioFundMetadata.PORTFOLIO_NAME_KEY, portfolioName);
		cv.put(PortfolioFundMetadata.FUND_ISIN_KEY, fundISIN);
		cv.put(PortfolioFundMetadata.FUND_BUYDATE_KEY, buyDate);
		cv.put(PortfolioFundMetadata.FUND_BUYPRICE_KEY, buyPrice);
		cv.put(PortfolioFundMetadata.FUND_ROUNDLOT_KEY, roundLot);
		database.insert(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, null, cv);
	}
	
	public void addNewShareInTransitionTable(String portfolioName, String shareISIN, String buyDate, float buyPrice, int roundLot)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioShareMetadata.ID, 1);
		cv.put(PortfolioShareMetadata.PORTFOLIO_NAME_KEY, portfolioName);
		cv.put(PortfolioShareMetadata.SHARE_ISIN_KEY, shareISIN);
		cv.put(PortfolioShareMetadata.SHARE_BUYDATE_KEY, buyDate);
		cv.put(PortfolioShareMetadata.SHARE_BUYPRICE_KEY, buyPrice);
		cv.put(PortfolioShareMetadata.SHARE_ROUNDLOT_KEY, roundLot);
		database.insert(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, null, cv);
	}
	
	public void addNewSiteForType(int version, String type, String site)
	{
		ContentValues cv = new ContentValues();
		cv.put(SiteTypeMetadata.ID, 1);
		cv.put(SiteTypeMetadata.VERSION, version);
		cv.put(SiteTypeMetadata.TYPE, type);
		cv.put(SiteTypeMetadata.SITE, site);
		database.insert(SiteTypeMetadata.SITE_TYPE_TABLE, null, cv);
	}
	
	
	
	//--------------------------------SELECT methods----------------------------//
	public Cursor getAllSavedPortfolio()
	{
		return database.query(PortfolioMetaData.PORTFOLIO_TABLE, null, null, null, null, null, null);
	}
	
	public Cursor getDetailsOfPortfolio(String portfolioName)
	{
		return database.query(PortfolioMetaData.PORTFOLIO_TABLE, null, PortfolioMetaData.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null, null, null, null);
	}
	
	//3 methods that return all Tools 'contenuti' in a specific Portfolio....
	public Cursor getAllBondsForPortfolio(String portfolioName)
	{
		return database.query(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, null, PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null, null, null, null);
	}
	
	public Cursor getAllFundsForPortfolio(String portfolioName)
	{
		return database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, null, PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null, null, null, null);
	}
	
	public Cursor getAllSharesForPortfolio(String portfolioName)
	{
		return database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, null, PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null, null, null, null);
	}
	
	//3 methods that return data of BOND/FUND/SHARE from specific transition table...
	public Cursor getSpecificBondOverviewInPortfolio(String portfolioName, String bondIsin, String purchaseDate)
	{
		return database.query(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, null, 
				PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioBondMetadata.BOND_ISIN_KEY+" = '"+bondIsin+"' AND "+PortfolioBondMetadata.BOND_BUYDATE_KEY+" = '"+purchaseDate+"'", null, null, null, null);
	}
	
	public Cursor getSpecificFundOverviewInPortfolio(String portfolioName, String fundIsin, String purchaseDate)
	{
		return database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, null, 
				PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioFundMetadata.FUND_ISIN_KEY+" = '"+fundIsin+"' AND "+PortfolioFundMetadata.FUND_BUYDATE_KEY+" = '"+purchaseDate+"'", null, null, null, null);
	}
	
	public Cursor getSpecificShareOverviewInPortfolio(String portfolioName, String shareIsin, String purchaseDate)
	{
		return database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, null, 
				PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioShareMetadata.SHARE_ISIN_KEY+" = '"+shareIsin+"' AND "+PortfolioShareMetadata.SHARE_BUYDATE_KEY+" = '"+purchaseDate+"'", null, null, null, null);
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
				"P."+PortfolioBondMetadata.BOND_BUYDATE_KEY, "P."+PortfolioBondMetadata.BOND_BUYPRICE_KEY, "P."+PortfolioBondMetadata.BOND_ROUNDLOT_KEY, 
				"B."+BondMetaData.BOND_VARIATION_KEY, "B."+BondMetaData.BOND_PERCVAR_KEY, "B."+BondMetaData.BOND_LASTCONTRACTPRICE_KEY+" as 'prezzo'"}, 
				"P."+PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioBondMetadata.BOND_ISIN_KEY+" = B.'"+BondMetaData.BOND_ISIN+"'", 
				null, null, null, null);
	}
	
	public Cursor getAllFundOverviewInPortfolio(String portfolioName)
	{
		return database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE+" as P join "+FundMetaData.FUND_TABLE+" as F", 
				new String[] {"P."+PortfolioFundMetadata.ID, "P."+PortfolioFundMetadata.PORTFOLIO_NAME_KEY, "P."+PortfolioFundMetadata.FUND_ISIN_KEY+" as 'isin'", 
				"P."+PortfolioFundMetadata.FUND_BUYDATE_KEY, "P."+PortfolioFundMetadata.FUND_BUYPRICE_KEY, "P."+PortfolioFundMetadata.FUND_ROUNDLOT_KEY, 
				"F."+FundMetaData.FUND_VARIATION_KEY, "F."+FundMetaData.FUND_PERCVAR_KEY, "F."+FundMetaData.FUND_LASTPRIZE_KEY+" as 'prezzo'"}, 
				"P."+PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioFundMetadata.FUND_ISIN_KEY+" = F.'"+FundMetaData.FUND_ISIN+"'", 
				null, null, null, null);
	}
	
	public Cursor getAllShareOverviewInPortfolio(String portfolioName)
	{
		return database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE+" as P join "+ShareMetaData.SHARE_TABLE+" as S", 
				new String[] {"P."+PortfolioShareMetadata.ID, "P."+PortfolioShareMetadata.PORTFOLIO_NAME_KEY, "P."+PortfolioShareMetadata.SHARE_ISIN_KEY+" as 'isin'", 
				"P."+PortfolioShareMetadata.SHARE_BUYDATE_KEY, "P."+PortfolioShareMetadata.SHARE_BUYPRICE_KEY, "P."+PortfolioShareMetadata.SHARE_ROUNDLOT_KEY, 
				"S."+ShareMetaData.SHARE_VARIATION_KEY, "S."+ShareMetaData.SHARE_PERCVAR_KEY, "S."+ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY+" as 'prezzo'"}, 
				"P."+PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' and P."+PortfolioShareMetadata.SHARE_ISIN_KEY+" = S.'"+ShareMetaData.SHARE_ISIN+"'", 
				null, null, null, null);
	}
	
	public Cursor getAllSitesForType()
	{
		return database.query(SiteTypeMetadata.SITE_TYPE_TABLE, null, null, null, null, null, null);
	}
	
	public Cursor getSitesForType(String type)
	{
		return database.query(SiteTypeMetadata.SITE_TYPE_TABLE, null, SiteTypeMetadata.TYPE+" = '"+type+"'", null, null, null, null);
	}
	
	public Cursor getHistoricalDataOfTool(String ISIN)
	{
		return database.query(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, HistoricalDataMetadata.ISIN+" = '"+ISIN+"'", null, null, null, HistoricalDataMetadata.ID);
	}
	
	public Cursor getHistoricalDataOfSHARE(String ISIN)
	{
		return database.query(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, HistoricalDataMetadata.ISIN+" = '"+ISIN+"'", null, null, null, HistoricalDataMetadata.ID);
	}
	
	//--------------------------------search index---------------------------------------//
	public int getLastIndexOfHistoricalData()
	{
		int result = 0;
		
		Cursor c = database.query(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, null, null, null, null, null);
		
		if(c.getCount() == 0)
		{
			c.close();
			return result;
		}
		else
		{
			c.close();
			return c.getCount();
		}
	}
	
	
	//--------------------------------boolean control methods----------------------------//
	public boolean bondAlreadyInDatabase(String bondIsin)
	{
		boolean result = false;
		
		Cursor c = database.query(BondMetaData.BOND_TABLE, null, BondMetaData.BOND_ISIN+" = '"+bondIsin+"'", null, null, null, null);
		
		if(c.getCount()>0)
		{
			result = true;
		}
		
		c.close();
		return result;
	}
	
	public boolean fundAlreadyInDatabase(String fundIsin)
	{
		boolean result = false;
		
		Cursor c = database.query(FundMetaData.FUND_TABLE, null, FundMetaData.FUND_ISIN+" = '"+fundIsin+"'", null, null, null, null);
		if(c.getCount()>0)
		{
			result = true;
		}
		
		c.close();
		return result;
	}
	
	public boolean shareAlreadyInDatabase(String shareIsin)
	{
		boolean result = false;
		
		Cursor c = database.query(ShareMetaData.SHARE_TABLE, null, ShareMetaData.SHARE_ISIN+" = '"+shareIsin+"'", null, null, null, null);
		if(c.getCount()>0)
		{
			result = true;
		}
		
		c.close();
		return result;
	}
	
	public boolean bondAlredyInTransitionTable(String isin){
		boolean result = false;
		Cursor c = database.query(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, null, PortfolioBondMetadata.BOND_ISIN_KEY+" = '"+isin+"'", null, null, null, null);
		if(c.getCount()>0)result = true;
		c.close();
		return result;
	}
	
	public boolean fundAlredyInTransitionTable(String isin){
		boolean result = false;
		Cursor c = database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, null, PortfolioFundMetadata.FUND_ISIN_KEY+" = '"+isin+"'", null, null, null, null);
		if(c.getCount()>0)result = true;
		c.close();
		return result;
	}
	
	public boolean shareAlredyInTransitionTable(String isin){
		boolean result = false;
		Cursor c = database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, null, PortfolioShareMetadata.SHARE_ISIN_KEY+" = '"+isin+"'", null, null, null, null);
		if(c.getCount()>0)result = true;
		c.close();
		return result;
	}
	
	public boolean bondUsefull(String ISIN)
	{
		boolean result = false;
		
		Cursor c = database.query(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, null, PortfolioBondMetadata.BOND_ISIN_KEY+" = '"+ISIN+"'", null, null, null, null);
		if(c.getCount()>0)
			result = true;
		c.close();
		
		return result;
	}
	
	public boolean fundUsefull(String ISIN)
	{
		boolean result = false;
		
		Cursor c = database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, null, PortfolioFundMetadata.FUND_ISIN_KEY+" = '"+ISIN+"'", null, null, null, null);
		if(c.getCount()>0)
			result = true;
		c.close();
		
		return result;
	}
	
	public boolean shareUsefull(String ISIN)
	{
		boolean result = false;
		
		Cursor c = database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, null, PortfolioShareMetadata.SHARE_ISIN_KEY+" = '"+ISIN+"'", null, null, null, null);
		if(c.getCount()>0)
			result = true;
		c.close();
		
		return result;
	}
	
	//control if other Portfolios contains the selected BOND ISIN..........................
	public boolean bondInOtherPortfolios(String ISIN, String Portfolio)
	{
		boolean result = false;
		
		Cursor c = database.query(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, null, PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" != '"+Portfolio+"' and "+PortfolioBondMetadata.BOND_ISIN_KEY+" = '"+ISIN+"'", null, null, null, null);
		if(c.getCount()>0)
		{
			result = true;
		}
		c.close();
		
		return result;
	}
	
	//control if other Portfolios contains the selected FUND ISIN..........................
	public boolean fundInOtherPortfolios(String ISIN, String Portfolio)
	{
		boolean result = false;
		
		Cursor c = database.query(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, null, PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" != '"+Portfolio+"' and "+PortfolioFundMetadata.FUND_ISIN_KEY+" = '"+ISIN+"'", null, null, null, null);
		if(c.getCount()>0)
		{
			result = true;
		}
		c.close();
		
		return result;
	}
	
	//control if other Portfolios contains the selected FUND ISIN..........................
	public boolean shareInOtherPortfolios(String ISIN, String Portfolio)
	{
		boolean result = false;
		
		Cursor c = database.query(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, null, PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" != '"+Portfolio+"' and "+PortfolioShareMetadata.SHARE_ISIN_KEY+" = '"+ISIN+"'", null, null, null, null);
		if(c.getCount()>0)
		{
			result = true;
		}
		c.close();
		
		return result;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//------------------------This 3 Methods returns all details -------------------------------------//
	//------------------------of a tool in a specific Portfolio---------------------------------------//
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Cursor getBondDetails(String ISIN)
	{
		return database.query(BondMetaData.BOND_TABLE, null, BondMetaData.BOND_ISIN+" = '"+ISIN+"'", null, null, null, null);
	}
	
	public Cursor getFundDetails(String ISIN)
	{
		return database.query(FundMetaData.FUND_TABLE, null, FundMetaData.FUND_ISIN+" = '"+ISIN+"'", null, null, null, null);
	}
	
	public Cursor getShareDetails(String CODE)
	{
		return database.query(ShareMetaData.SHARE_TABLE, null, ShareMetaData.SHARE_ISIN+" = '"+CODE+"'", null, null, null, null);
	}
	
	
	//--------------------------------UPDATE methods----------------------------//
	
	public void updateSelectedPortfolio(String previousName, String newName, String newDescription, String newDate, String newLastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioMetaData.PORTFOLIO_NAME_KEY, newName);
		cv.put(PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY, newDescription);
		cv.put(PortfolioMetaData.PORTFOLIO_CREATION_DATE_KEY, newDate);
		cv.put(PortfolioMetaData.PORTFOLIO_LASTUPDATE_KEY, newLastUpdate);
		database.update(PortfolioMetaData.PORTFOLIO_TABLE, cv, PortfolioMetaData.PORTFOLIO_NAME_KEY+" = '"+previousName+"'", null);
	}
	
	public void updateSelectedPortfolioLastUpdate(String portfolioName, String newLastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioMetaData.PORTFOLIO_LASTUPDATE_KEY, newLastUpdate);
		database.update(PortfolioMetaData.PORTFOLIO_TABLE, cv, PortfolioMetaData.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null);
	}
	
	//3 methods that update selected BOND/FUND/SHARE in transition table
	public void updateSelectedBondInTransitionTable(String portfolioName, String bondIsin, String oldPurchaseDate, String newPurchaseDate, Float newPrize, int newLot)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioBondMetadata.BOND_BUYDATE_KEY, newPurchaseDate);
		cv.put(PortfolioBondMetadata.BOND_BUYPRICE_KEY, newPrize);
		cv.put(PortfolioBondMetadata.BOND_ROUNDLOT_KEY, newLot);
		database.update(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, cv, PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioBondMetadata.BOND_ISIN_KEY+" = '"+bondIsin+"' AND "+PortfolioBondMetadata.BOND_BUYDATE_KEY+" = '"+oldPurchaseDate+"'", null);
	}
	
	public void updateSelectedFundInTransitionTable(String portfolioName, String fundIsin, String oldPurchaseDate, String newPurchaseDate, Float newPrize, int newLot)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioFundMetadata.FUND_BUYDATE_KEY, newPurchaseDate);
		cv.put(PortfolioFundMetadata.FUND_BUYPRICE_KEY, newPrize);
		cv.put(PortfolioFundMetadata.FUND_ROUNDLOT_KEY, newLot);
		database.update(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, cv, PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioFundMetadata.FUND_ISIN_KEY+" = '"+fundIsin+"' AND "+PortfolioFundMetadata.FUND_BUYDATE_KEY+" = '"+oldPurchaseDate+"'", null);
	}
	
	public void updateSelectedShareInTransitionTable(String portfolioName, String shareIsin, String oldPurchaseDate, String newPurchaseDate, Float newPrize, int newLot)
	{
		ContentValues cv = new ContentValues();
		cv.put(PortfolioShareMetadata.SHARE_BUYDATE_KEY, newPurchaseDate);
		cv.put(PortfolioShareMetadata.SHARE_BUYPRICE_KEY, newPrize);
		cv.put(PortfolioShareMetadata.SHARE_ROUNDLOT_KEY, newLot);
		database.update(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, cv, PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioShareMetadata.SHARE_ISIN_KEY+" = '"+shareIsin+"' AND "+PortfolioShareMetadata.SHARE_BUYDATE_KEY+" = '"+oldPurchaseDate+"'", null);
	}
	
//	public void updateSelectedBond(String ISIN, String name, String currency, String market, String marketPhase, float lastContractPrice, 
//			float percVariation, float variation, String lastContractDate, int lastVolume, int buyVolume, int sellVolume, 
//			int totalVolume, float buyPrice, float sellPrice, float maxToday, float minToday, float maxYear, float minYear, 
//			String maxYearDate, String minYearDate, float lastClose, String expirationDate, String couponDate, float coupon, 
//			int minRoundLot, String lastUpdate, String sourceSite, String preferredSite, String ignoredSites)
//	{
//		ContentValues cv = new ContentValues();
//		cv.put(BondMetaData.BOND_ISIN, ISIN); //no perch� serve per identificare la tupla
//		cv.put(BondMetaData.BOND_NAME_KEY, name); //il nome potrebbe cambiare...
//		cv.put(BondMetaData.BOND_CURRENCY_KEY, currency);
//		cv.put(BondMetaData.BOND_MARKET_KEY, market);
//		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, marketPhase);
//		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, lastContractPrice);
//		cv.put(BondMetaData.BOND_PERCVAR_KEY, percVariation);
//		cv.put(BondMetaData.BOND_VARIATION_KEY, variation);
//		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, lastContractDate);
//		cv.put(BondMetaData.BOND_LASTVOLUME_KEY, lastVolume);
//		cv.put(BondMetaData.BOND_BUYVOLUME_KEY, buyVolume);
//		cv.put(BondMetaData.BOND_SELLVOLUME_KEY, sellVolume);
//		cv.put(BondMetaData.BOND_TOTALVOLUME_KEY, totalVolume);
//		cv.put(BondMetaData.BOND_BUYPRICE_KEY, buyPrice);
//		cv.put(BondMetaData.BOND_SELLPRICE_KEY, sellPrice);
//		cv.put(BondMetaData.BOND_MAXTODAY_KEY, maxToday);
//		cv.put(BondMetaData.BOND_MINTODAY_KEY, minToday);
//		cv.put(BondMetaData.BOND_MAXYEAR_KEY, maxYear);
//		cv.put(BondMetaData.BOND_MINYEAR_KEY, minYear);
//		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, maxYearDate);
//		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, minYearDate);
//		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, lastClose);
//		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, expirationDate);
//		cv.put(BondMetaData.BOND_COUPONDATE_KEY, couponDate);
//		cv.put(BondMetaData.BOND_COUPON_KEY, coupon);
//		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, minRoundLot);
//		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
//		cv.put(BondMetaData.BOND_SOURCESITE_KEY, sourceSite);
//		cv.put(BondMetaData.BOND_PREFERREDSITE_KEY, preferredSite);
//		cv.put(BondMetaData.BOND_IGNOREDSITES_KEY, ignoredSites);
//		database.update(BondMetaData.BOND_TABLE, cv, BondMetaData.BOND_ISIN+" = '"+ISIN+"'", null);
//	}
	
	public void updateSelectedBondByQuotationObject(Quotation_Bond newBond, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
//		cv.put(BondMetaData.ID, 1); 
//		cv.put(BondMetaData.BOND_ISIN, newBond.getISIN()); 
		cv.put(BondMetaData.BOND_NAME_KEY, newBond.getName()); //il nome potrebbe cambiare, l'ISIN no!
		cv.put(BondMetaData.BOND_CURRENCY_KEY, newBond.getValuta());
		cv.put(BondMetaData.BOND_MARKET_KEY, newBond.getMercato());
		cv.put(BondMetaData.BOND_MARKETPHASE_KEY, newBond.getFaseMercato());
		cv.put(BondMetaData.BOND_LASTCONTRACTPRICE_KEY, newBond.getPrezzoUltimoContratto());
		cv.put(BondMetaData.BOND_PERCVAR_KEY, newBond.getVariazionePercentuale());
		cv.put(BondMetaData.BOND_VARIATION_KEY, newBond.getVariazioneAssoluta());
		cv.put(BondMetaData.BOND_LASTCONTRACTDATE_KEY, newBond.getDataUltimoContratto());
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
		cv.put(BondMetaData.BOND_MAXYEARDATE_KEY, newBond.getDataMaxAnno());
		cv.put(BondMetaData.BOND_MINYEARDATE_KEY, newBond.getDataMinAnno());
		cv.put(BondMetaData.BOND_LASTCLOSE_KEY, newBond.getAperturaChiusuraPrecedente());
		cv.put(BondMetaData.BOND_EXPIRATIONDATE_KEY, newBond.getScadenza());
		cv.put(BondMetaData.BOND_COUPONDATE_KEY, newBond.getDataStaccoCedola());
		cv.put(BondMetaData.BOND_COUPON_KEY, newBond.getCedola());
		cv.put(BondMetaData.BOND_MINROUNDLOT_KEY, newBond.getLottoMinimo());
		cv.put(BondMetaData.BOND_LASTUPDATE_KEY, lastUpdate);
		cv.put(BondMetaData.BOND_SOURCESITE_KEY, newBond.getSite());
		cv.put(BondMetaData.BOND_SOURCESITEURL_KEY, newBond.getSiteUrl());
//		cv.put(BondMetaData.BOND_PREFERREDSITE_KEY, "");
//		cv.put(BondMetaData.BOND_IGNOREDSITES_KEY, "");
		database.update(BondMetaData.BOND_TABLE, cv, BondMetaData.BOND_ISIN+" = '"+newBond.getISIN()+"'", null);
		
		ContentValues cv2 = new ContentValues();
		cv2.put(HistoricalDataMetadata.ID, getLastIndexOfHistoricalData()+1);
		cv2.put(HistoricalDataMetadata.ISIN, newBond.getISIN());
		cv2.put(HistoricalDataMetadata.DATE, lastUpdate);
		cv2.put(HistoricalDataMetadata.VALUE, String.valueOf(newBond.getPrezzoUltimoContratto()));
		database.insert(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, cv2);
	}
	
//	public void updateSelectedFund(String ISIN, String name, String manager, String category, String benchmark, 
//			float lastPrize, String lastPrizeDate, float precPrize, String currency, float percVariation, float variation, 
//			float performance1Month, float performance3Month, float performance1Year, float performance3Year, String lastUpdate, 
//			String sourceSite, String preferredSite, String ignoredSites)
//	{
//		ContentValues cv = new ContentValues();
//		cv.put(FundMetaData.FUND_ISIN, ISIN);// no serve per la ricerca
//		cv.put(FundMetaData.FUND_NAME_KEY, name);// no potrebbe cambiare
//		cv.put(FundMetaData.FUND_MANAGER_KEY, manager);
//		cv.put(FundMetaData.FUND_CATEGORY_KEY, category);
//		cv.put(FundMetaData.FUND_BENCHMARK_KEY, benchmark);
//		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, lastPrize);
//		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, lastPrizeDate);
//		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, precPrize);
//		cv.put(FundMetaData.FUND_CURRENCY_KEY, currency);
//		cv.put(FundMetaData.FUND_PERCVAR_KEY, percVariation);
//		cv.put(FundMetaData.FUND_VARIATION_KEY, variation);
//		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, performance1Month);
//		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, performance3Month);
//		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, performance1Year);
//		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, performance3Year);
//		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
//		cv.put(FundMetaData.FUND_SOURCESITE_KEY, sourceSite);
//		cv.put(FundMetaData.FUND_PREFERREDSITE_KEY, preferredSite);
//		cv.put(FundMetaData.FUND_IGNOREDSITES_KEY, ignoredSites);
//		database.update(FundMetaData.FUND_TABLE, cv, FundMetaData.FUND_ISIN+" = '"+ISIN+"'", null);
//
//	}
	
	public void updateSelectedFundByQuotationObject(Quotation_Fund newFund, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
//		cv.put(FundMetaData.FUND_ISIN, newFund.getISIN()); // ometto?
		cv.put(FundMetaData.FUND_NAME_KEY, newFund.getName()); // no potrebbe cambiare
		cv.put(FundMetaData.FUND_MANAGER_KEY, newFund.getNomeGestore());
		cv.put(FundMetaData.FUND_CATEGORY_KEY, newFund.getCategoriaAssociati());
		cv.put(FundMetaData.FUND_BENCHMARK_KEY, newFund.getBenchmarkDichiarato());
		cv.put(FundMetaData.FUND_LASTPRIZE_KEY, newFund.getUltimoPrezzo());
		cv.put(FundMetaData.FUND_LASTPRIZEDATE_KEY, newFund.getDataUltimoPrezzo());
		cv.put(FundMetaData.FUND_PRECPRIZE_KEY, newFund.getPrezzoPrecedente());
		cv.put(FundMetaData.FUND_CURRENCY_KEY, newFund.getValuta());
		cv.put(FundMetaData.FUND_PERCVAR_KEY, newFund.getVariazionePercentuale());
		cv.put(FundMetaData.FUND_VARIATION_KEY, newFund.getVariazioneAssoluta());
		cv.put(FundMetaData.FUND_PERFORMANCE1MONTH, newFund.getPerformance1Mese());
		cv.put(FundMetaData.FUND_PERFORMANCE3MONTH, newFund.getPerformance3Mesi());
		cv.put(FundMetaData.FUND_PERFORMANCE1YEAR, newFund.getPerformance1Anno());
		cv.put(FundMetaData.FUND_PERFORMANCE3YEAR, newFund.getPerformance3Anni());
		cv.put(FundMetaData.FUND_LASTUPDATE_KEY, lastUpdate);
		cv.put(FundMetaData.FUND_SOURCESITE_KEY, newFund.getSite());
		cv.put(FundMetaData.FUND_SOURCESITEURL_KEY, newFund.getSiteUrl());
//		cv.put(FundMetaData.FUND_PREFERREDSITE_KEY, "");
//		cv.put(FundMetaData.FUND_IGNOREDSITES_KEY, "");
		database.update(FundMetaData.FUND_TABLE, cv, FundMetaData.FUND_ISIN+" = '"+newFund.getISIN()+"'", null);
		
		ContentValues cv2 = new ContentValues();
		cv2.put(HistoricalDataMetadata.ID, getLastIndexOfHistoricalData()+1);
		cv2.put(HistoricalDataMetadata.ISIN, newFund.getISIN());
		cv2.put(HistoricalDataMetadata.DATE, lastUpdate);
		cv2.put(HistoricalDataMetadata.VALUE, String.valueOf(newFund.getUltimoPrezzo()));
		database.insert(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, null, cv2);
	}
	
//	public void updateSelectedShare(String CODE, String isin, String name, int minRoundLot, String marketPhase, float lastContractPrice, 
//			float percVariation, float variation, String lastContractDate, float buyPrice, float sellPrice, int lastAmount, 
//			int buyAmount, int sellAmount, int totalAmount, float maxToday, float minToday, float maxYear, float minYear, 
//			String maxYearDate, String minYearDate, float lastClose, String lastUpdate, 
//			String sourceSite, String preferredSite, String ignoredSites)
//	{
//		ContentValues cv = new ContentValues();
//		cv.put(ShareMetaData.SHARE_CODE, CODE); //no serve per ricerca
//		cv.put(ShareMetaData.SHARE_ISIN, isin); //
//		cv.put(ShareMetaData.SHARE_NAME_KEY, name); //no potrebbe cambiare
//		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, minRoundLot);
//		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, marketPhase);
//		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, lastContractPrice);
//		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, percVariation);
//		cv.put(ShareMetaData.SHARE_VARIATION_KEY, variation);
//		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, lastContractDate);
//		cv.put(ShareMetaData.SHARE_BUYPRICE_KEY, buyPrice);
//		cv.put(ShareMetaData.SHARE_SELLPRICE_KEY, sellPrice);
//		cv.put(ShareMetaData.SHARE_LASTAMOUNT_KEY, lastAmount);
//		cv.put(ShareMetaData.SHARE_BUYAMOUNT_KEY, buyAmount);
//		cv.put(ShareMetaData.SHARE_SELLAMOUNT_KEY, sellAmount);
//		cv.put(ShareMetaData.SHARE_TOTALAMOUNT_KEY, totalAmount);
//		cv.put(ShareMetaData.SHARE_MAXTODAY_KEY, maxToday);
//		cv.put(ShareMetaData.SHARE_MINTODAY_KEY, minToday);
//		cv.put(ShareMetaData.SHARE_MAXYEAR_KEY, maxYear);
//		cv.put(ShareMetaData.SHARE_MINYEAR_KEY, minYear);
//		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, maxYearDate);
//		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, minYearDate);
//		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, lastClose);
//		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
//		cv.put(ShareMetaData.SHARE_SOURCESITE_KEY, sourceSite);
//		cv.put(ShareMetaData.SHARE_PREFERREDSITE_KEY, preferredSite);
//		cv.put(ShareMetaData.SHARE_IGNOREDSITES_KEY, ignoredSites);
//		database.update(ShareMetaData.SHARE_TABLE, cv, ShareMetaData.SHARE_CODE+" = '"+CODE+"'", null);
//	}
	
	public void updateSelectedShareByQuotationObject(Quotation_Share newShare, String lastUpdate)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.SHARE_CODE, newShare.getISIN());
		cv.put(ShareMetaData.SHARE_ISIN, newShare.getISIN());
		cv.put(ShareMetaData.SHARE_NAME_KEY, newShare.getName());
		cv.put(ShareMetaData.SHARE_MINROUNDLOT_KEY, newShare.getLottoMinimo());
		cv.put(ShareMetaData.SHARE_MARKETPHASE_KEY, newShare.getFaseMercato());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTPRICE_KEY, newShare.getPrezzoUltimoContratto());
		cv.put(ShareMetaData.SHARE_PERCVAR_KEY, newShare.getVariazionePercentuale());
		cv.put(ShareMetaData.SHARE_VARIATION_KEY, newShare.getVariazioneAssoluta());
		cv.put(ShareMetaData.SHARE_LASTCONTRACTDATE_KEY, newShare.getDataOraUltimoAcquisto());
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
		cv.put(ShareMetaData.SHARE_MAXYEARDATE_KEY, newShare.getDataMaxAnno());
		cv.put(ShareMetaData.SHARE_MINYEARDATE_KEY, newShare.getDataMinAnno());
		cv.put(ShareMetaData.SHARE_LASTCLOSE_KEY, newShare.getChiusuraPrecedente());
		cv.put(ShareMetaData.SHARE_LASTUPDATE_KEY, lastUpdate);
		cv.put(ShareMetaData.SHARE_SOURCESITE_KEY, newShare.getSite());
		cv.put(ShareMetaData.SHARE_SOURCESITEURL_KEY, newShare.getSiteUrl());
//		cv.put(ShareMetaData.SHARE_PREFERREDSITE_KEY, "");
//		cv.put(ShareMetaData.SHARE_IGNOREDSITES_KEY, "");
		database.update(ShareMetaData.SHARE_TABLE, cv, ShareMetaData.SHARE_ISIN+" = '"+newShare.getISIN()+"'", null);
	}

	//managment of preferred and ignored sites......
	public void updateSelectedBondPreferredSite(String bondIsin, String PreferredSite)
	{
		ContentValues cv = new ContentValues();
		cv.put(BondMetaData.BOND_PREFERREDSITE_KEY, PreferredSite);
		database.update(BondMetaData.BOND_TABLE, cv, BondMetaData.BOND_ISIN+" = '"+bondIsin+"'", null);
	}
	
	public void updateSelectedFundPreferredSite(String fundIsin, String PreferredSite)
	{
		ContentValues cv = new ContentValues();
		cv.put(FundMetaData.FUND_PREFERREDSITE_KEY, PreferredSite);
		database.update(FundMetaData.FUND_TABLE, cv, FundMetaData.FUND_ISIN+" = '"+fundIsin+"'", null);
	}
	
	public void updateSelectedSharePreferredSite(String shareIsin, String PreferredSite)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.SHARE_PREFERREDSITE_KEY, PreferredSite);
		database.update(ShareMetaData.SHARE_TABLE, cv, ShareMetaData.SHARE_ISIN+" = '"+shareIsin+"'", null);
	}
	
	public void updateSelectedBondIgnoredSites(String bondIsin, String ignoredSites)
	{
		ContentValues cv = new ContentValues();
		cv.put(BondMetaData.BOND_IGNOREDSITES_KEY, ignoredSites);
		database.update(BondMetaData.BOND_TABLE, cv, BondMetaData.BOND_ISIN+" = '"+bondIsin+"'", null);
	}
	
	public void updateSelectedFundIgnoredSites(String fundIsin, String ignoredSites)
	{
		ContentValues cv = new ContentValues();
		cv.put(FundMetaData.FUND_IGNOREDSITES_KEY, ignoredSites);
		database.update(FundMetaData.FUND_TABLE, cv, FundMetaData.FUND_ISIN+" = '"+fundIsin+"'", null);
	}
	
	public void updateSelectedShareIgnoredSites(String shareCode, String ignoredSites)
	{
		ContentValues cv = new ContentValues();
		cv.put(ShareMetaData.SHARE_IGNOREDSITES_KEY, ignoredSites);
		database.update(ShareMetaData.SHARE_TABLE, cv, ShareMetaData.SHARE_ISIN+" = '"+shareCode+"'", null);
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
	
	public void deleteShare(String ISIN)
	{
		database.delete(ShareMetaData.SHARE_TABLE, ShareMetaData.SHARE_ISIN+" = '"+ISIN+"'", null);
	}
	
	public void deleteBondInTransitionTable(String portfolioName, String ISIN, String purchaseDate) 
	{
		
		database.delete(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, PortfolioBondMetadata.PORTFOLIO_NAME_KEY + " = '" + portfolioName +"' AND "+ PortfolioBondMetadata.BOND_ISIN_KEY + " = '" + ISIN + "' AND " + PortfolioBondMetadata.BOND_BUYDATE_KEY + " = '"  + purchaseDate+"'", null);
	}
		
	public void deleteFundInTransitionTable(String portfolioName, String ISIN, String purchaseDate) 
	{
		database.delete(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioFundMetadata.FUND_ISIN_KEY+" = '"+ISIN+"' AND "+PortfolioFundMetadata.FUND_BUYDATE_KEY+" = '"+purchaseDate+"'", null);
	}
	
	public void deleteShareInTransitionTable(String portfolioName, String CODE, String purchaseDate) 
	{
		database.delete(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"' AND "+PortfolioShareMetadata.SHARE_ISIN_KEY+" = '"+CODE+"' AND "+PortfolioShareMetadata.SHARE_BUYDATE_KEY+" = '"+purchaseDate+"'", null);
	}
	
	public void deleteAllBondsInTransitionTableForPortfolio(String portfolioName)
	{
		database.delete(PortfolioBondMetadata.PORTFOLIO_BOND_TABLE, PortfolioBondMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null);
	}
	
	public void deleteAllFundsInTransitionTableForPortfolio(String portfolioName)
	{
		database.delete(PortfolioFundMetadata.PORTFOLIO_FUND_TABLE, PortfolioFundMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null);
	}
	
	public void deleteAllSharesInTransitionTableForPortfolio(String portfolioName)
	{
		database.delete(PortfolioShareMetadata.PORTFOLIO_SHARE_TABLE, PortfolioShareMetadata.PORTFOLIO_NAME_KEY+" = '"+portfolioName+"'", null);
	}
	
	public void deleteTOOLHistoricalData(String ISIN)
	{
		database.delete(HistoricalDataMetadata.HISTORICAL_DATA_TABLE, HistoricalDataMetadata.ISIN+" = '"+ISIN+"'", null);
	}
	
	public void deleteAllSitesForTypes()
	{
		database.delete(SiteTypeMetadata.SITE_TYPE_TABLE, null, null);
	}
}
