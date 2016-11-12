package com.youtube.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.google.android.gms.ads.AdView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.youtube.parse.SPManager;
import com.youtube.util.Helper;

public class UpgradeActivity extends Activity {
	IabHelper mHelper;
	AdView mAdView;
	Context con;
	static final String SKU_BRONZE_INFINITE = "bronze";

	static final String SKU_GOLD = "gold";
	static final String SKU_SILVER = "silver";
	static final String SKU_PLATINUM = "platinum";

	static int PURCHASE_REQUEST = 1011;

	void UpdateButtonsWithPrices(List<ParseObject> answers) {
		if (answers == null)
			return;
		if (answers.size() < 4)
			return;

		// ---------------- BRONZE -----------------------
		Button bt = (Button) findViewById(R.id.upgrades_button_bronze);
		bt.setText(bt.getText() + (answers.get(0).getNumber("price") + "$"));
		bt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String payload = SPManager.current_user.getObjectId();
				mHelper.launchPurchaseFlow((Activity) arg0.getContext(),
						SKU_BRONZE_INFINITE, PURCHASE_REQUEST,
						mPurchaseFinishedListener, payload);
			}
		});
		// ---------------- SILVER -----------------------
		bt = (Button) findViewById(R.id.upgrades_button_silver);
		bt.setText(bt.getText() + (answers.get(1).getNumber("price") + "$"));
		bt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String payload = SPManager.current_user.getObjectId();
				mHelper.launchPurchaseFlow((Activity) arg0.getContext(),
						SKU_SILVER, PURCHASE_REQUEST,
						mPurchaseFinishedListener, payload);
			}
		});
		// ---------------- GOLD -----------------------
		bt = (Button) findViewById(R.id.upgrades_button_gold);
		bt.setText(bt.getText() + (answers.get(2).getNumber("price") + "$"));
		bt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Intent i = new Intent(getApplicationContext(),
				// LearnMoreActivity.class);
				// startActivity(i);
			}
		});
		// ---------------- PLATINUM -----------------------
		bt = (Button) findViewById(R.id.upgrades_button_platinum);
		bt.setText(bt.getText() + (answers.get(3).getNumber("price") + "$"));
		bt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Intent i = new Intent(getApplicationContext(),
				// LearnMoreActivity.class);
				// startActivity(i);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
		// + data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		con = getApplicationContext();
		// Action bar stuff

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setHomeButtonEnabled(true);
		// ---
		// --------------- Billing stuff
		String base64EncodedPublicKey = "AIzaSyB4_dBTcB0LJrCGTgULyq4xBLZ5OoHqEP4";

		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// bIND

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Helper.ShowDialogue("Billing",
							"Problem setting up In-app Billing: " + result,
							getApplicationContext());
				} else {
					Helper.ShowDialogue("Billing", "All good " + result,
							getApplicationContext());
				}

			}
		});
		// ------------------

		setContentView(R.layout.upgrade_activity);
		// -- ads stuff
		mAdView = (AdView) findViewById(R.id.upgrades_banner);
		Helper.MakeAdDecision(mAdView);

		// ------------update the button prices

		ParseQuery<ParseObject> computeQuery = ParseQuery.getQuery("Upgrades");
		computeQuery.orderByAscending("Order");
		try {
			computeQuery.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> answer, ParseException arg1) {
					UpdateButtonsWithPrices(answer);
				}
			});

		} catch (Exception er) {
			Helper.ShowDialogue(er.getMessage(),
					": Download error, try again later",
					getApplicationContext());
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}

	// ----------------------- PURCHASING LOGIC

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			// Log.d(TAG, "Purchase finished: " + result + ", purchase: " +
			// purchase);
			
			
			if (purchase == null) {
				Helper.ShowDialogue("Error ", "purchase is null", con);
				return;
			}
			
			String payload = purchase.getDeveloperPayload();
			String mySKU = purchase.getSku();
			String token = purchase.getToken();
			
			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				Helper.ShowDialogue("Error Purchasing: ", result.getMessage(),
						con);
				// setWaitScreen(false);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				Helper.ShowDialogue("Error purchasing. Authenticity verification failed.", "", con);
				// setWaitScreen(false);
				return;
			}

			// Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(SKU_SILVER)) {
				// bought 1/4 tank of gas. So consume it.
				// Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}else if (purchase.getSku().equals(SKU_GOLD)) {
				// bought 1/4 tank of gas. So consume it.
				// Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			} else if (purchase.getSku().equals(SKU_PLATINUM)) {
				// bought 1/4 tank of gas. So consume it.
				// Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			} else if (purchase.getSku().equals(SKU_BRONZE_INFINITE)) {
				// bought the premium upgrade!
				// Log.d(TAG,
				// "Purchase is premium upgrade. Congratulating user.");
				// alert("Thank you for upgrading to premium!");
				// mIsPremium = true;
				// updateUi();
				// setWaitScreen(false);
			} //else if (purchase.getSku().equals(SKU_GOLD)) {
				// bought the infinite gas subscription
				// Log.d(TAG, "Infinite gas subscription purchased.");
				// alert("Thank you for subscribing to infinite gas!");
				// mSubscribedToInfiniteGas = true;
				// mTank = TANK_MAX;
				// updateUi();
				// setWaitScreen(false);
			//}
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			// Log.d(TAG, "Consumption finished. Purchase: " + purchase +
			// ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				// Log.d(TAG, "Consumption successful. Provisioning.");
				// mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
				// saveData();
				// alert("You filled 1/4 tank. Your tank is now " +
				// String.valueOf(mTank) + "/4 full!");
			} else {
				// complain("Error while consuming: " + result);
			}
			// updateUi();
			// setWaitScreen(false);
			// Log.d(TAG, "End consumption flow.");
		}
	};

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			// Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				// complain("Failed to query inventory: " + result);
				return;
			}

			// Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Do we have the premium upgrade?
			// Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
			// mIsPremium = (premiumPurchase != null &&
			// verifyDeveloperPayload(premiumPurchase));
			// Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" :
			// "NOT PREMIUM"));

			// Do we have the infinite gas plan?
			// Purchase infiniteGasPurchase =
			// inventory.getPurchase(SKU_INFINITE_GAS);
			// mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
			// verifyDeveloperPayload(infiniteGasPurchase));
			// Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" :
			// "DOES NOT HAVE")
			// + " infinite gas subscription.");
			// if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

			// Check for gas delivery -- if we own gas, we should fill up the
			// tank immediately
			// Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
			// if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
			// / Log.d(TAG, "We have gas. Consuming it.");
			// mHelper.consumeAsync(inventory.getPurchase(SKU_GAS),
			// mConsumeFinishedListener);
			// return;
			// }

			// updateUi();
			// setWaitScreen(false);
			// Log.d(TAG,
			// "Initial inventory query finished; enabling main UI.");
		}
	};

	// -------------------------- purchase helper functions -------------------
	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		return true;
	}

}