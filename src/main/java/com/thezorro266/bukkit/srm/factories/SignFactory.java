/**
 * SimpleRegionMarket
 * Copyright (C) 2013  theZorro266 <http://www.thezorro266.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.thezorro266.bukkit.srm.factories;

import lombok.Data;
import lombok.Getter;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;

import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.helpers.Location;

public class SignFactory {
	public static final SignFactory instance = new SignFactory();

	private SignFactory() {
	}

	@Getter
	private int signCount = 0;

	public @Data
	class Sign {
		public static final int SIGN_LINE_COUNT = 4;

		final Region region;
		final Location location;
		final boolean isWallSign;
		final BlockFace direction;

		private Sign(Region region, Location location, boolean isWallSign, BlockFace direction) {
			if (region == null) {
				throw new IllegalArgumentException("Region must not be null");
			}
			if (location == null) {
				throw new IllegalArgumentException("Location must not be null");
			}
			if (direction == null) {
				throw new IllegalArgumentException("Direction must not be null");
			}

			this.region = region;
			this.location = new Location(location);
			this.isWallSign = isWallSign;
			this.direction = direction;
		}

		public void clear() {
			setContent(new String[] { "", "", "", "" });
		}

		public void setContent(String[] lines) {
			Block signBlock = location.getBlock();
			org.bukkit.block.Sign signBlockState = (org.bukkit.block.Sign) signBlock.getState();
			if (!isSign(signBlock)) {
				signBlock.setType(isWallSign ? Material.WALL_SIGN : Material.SIGN_POST);
				((org.bukkit.material.Sign) signBlockState.getData()).setFacingDirection(direction);
			}

			for (int i = 0; i < SIGN_LINE_COUNT; i++) {
				signBlockState.setLine(i, lines[i]);
			}

			signBlockState.update(false, false);
		}

		public boolean isBlockThisSign(Block block) {
			if (isSign(block)) {
				if (location.isBlockAt(block)) {
					return true;
				}
			}
			return false;
		}

		public void saveToConfiguration(Configuration config, String path) {
			config.set(path + "region", region.getName());
			location.saveToConfiguration(config, path + "location.");
			config.set(path + "is_wall_sign", isWallSign);
			config.set(path + "direction", direction.toString());
		}

		@Override
		public String toString() {
			return String.format("Sign[r:%s,l:%s]", region.getName(), location);
		}
	}

	public Sign createSign(Region region, Location location, boolean isWallSign, BlockFace direction) {
		// Check for sign on location
		Sign oldSign = SimpleRegionMarket.getInstance().getLocationSignHelper().getSign(location);
		if (oldSign != null) {
			throw new IllegalArgumentException("Location already has a sign");
		}

		// Create new sign
		Sign sign = new Sign(region, location, isWallSign, direction);

		region.getSignList().add(sign);
		SimpleRegionMarket.getInstance().getLocationSignHelper().addSignAndLocation(sign);

		++signCount;

		return sign;
	}

	public void destroySign(Sign sign) {
		sign.getRegion().getSignList().remove(sign);
		SimpleRegionMarket.getInstance().getLocationSignHelper().removeSignAndLocation(sign);

		--signCount;
	}

	public boolean isSign(Block block) {
		if (block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST)) {
			return true;
		}
		return false;
	}

	public Sign getSignFromLocation(Location location) {
		return SimpleRegionMarket.getInstance().getLocationSignHelper().getSign(location);
	}

	public Sign loadFromConfiguration(Configuration config, Region region, String path) throws ContentLoadException {
		String regionName = region.getName();
		String configRegionName = config.getString(path + "region");
		if (regionName.equals(configRegionName)) {
			Location location = Location.loadFromConfiguration(config, path + "location.");
			boolean isWallSign = config.getBoolean(path + "is_wall_sign");
			BlockFace direction = BlockFace.valueOf(config.getString(path + "direction"));

			return createSign(region, location, isWallSign, direction);
		} else {
			throw new ContentLoadException("Region string in sign config did not match the outer region");
		}
	}
}