package sancho.lossimpleprofileswitcher;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import java.util.UUID;

import cyanogenmod.app.Profile;
import cyanogenmod.app.ProfileManager;

public class ProfileTileService extends TileService
{
	@Override
	public void onStartListening()
	{
		super.onStartListening();
		ProfileManager profilemanager = ProfileManager.getInstance(this);
		Tile tile = this.getQsTile();
		tile.setContentDescription(getString(R.string.tile_name));
		tile.updateTile();
		try
		{
			Profile profile = profilemanager.getActiveProfile();
			tile.setLabel(profile.getName());
			tile.updateTile();
		}
		catch (Exception e)
		{
			Toast.makeText(this, R.string.pm_notavailable, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick()
	{
		try
		{
			ProfileManager profilemanager = ProfileManager.getInstance(this);
			Profile[] profiles = profilemanager.getProfiles();
			Profile current = profilemanager.getActiveProfile();

			if(isLocked() && current.getScreenLockMode().getValue() != Profile.LockMode.INSECURE)
			{
				// prevent changing profiles with locked screen
				return;
			}

			UUID next = current.getUuid(); // in case it doesn't switch
			for (int i = 0; i < profiles.length; i++)
			{
				if (profiles[i].getUuid().equals(current.getUuid()))
				{
					if (i < profiles.length - 1)
					{
						next = profiles[i + 1].getUuid();
					}
					else
					{
						// last item
						next = profiles[0].getUuid();
					}
					break;
				}
			}
			profilemanager.setActiveProfile(next);

			Tile tile = this.getQsTile();
			tile.setLabel(profilemanager.getActiveProfile().getName());
			tile.updateTile();
		}
		catch (Exception e)
		{
			Toast.makeText(this, R.string.pm_notavailable, Toast.LENGTH_SHORT).show();
		}
	}
}
