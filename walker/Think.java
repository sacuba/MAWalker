package walker;

import info.Card;

import java.util.List;

import action.ActionRegistry;
import action.ActionRegistry.Action;

import walker.Process;

public class Think {
	
	private static final String AP_HALF = "101";
	private static final String BC_HALF = "111";
	private static final String AP_FULL = "1";
	private static final String BC_FULL = "2";
	
	private static final int EXPLORE_NORMAL = 60;
	private static final int EXPLORE_URGENT = 80;
	private static final int GFL_PRI = 70;
	private static final int GF_PRI = 25;
	private static final int USE_PRI = 99;
	public static ActionRegistry.Action doIt (List<ActionRegistry.Action> possible) {
		Action best = Action.NOTHING;
		int score = Integer.MIN_VALUE + 20;
		for (int i = 0; i < possible.size(); i++) {
			switch (possible.get(i)) {
			case LOGIN:
				return ActionRegistry.Action.LOGIN;
			case ADD_AREA:
				return Action.ADD_AREA;
			case GET_FLOOR_INFO:
				return Action.GET_FLOOR_INFO;
			case GET_FAIRY_LIST:
				if (score < GFL_PRI) {
					best = Action.GET_FAIRY_LIST;
					score = GFL_PRI;
				}
				break;
			case GOTO_FLOOR:
				if (score < GF_PRI) {
					best = Action.GOTO_FLOOR;
					score = GF_PRI;
				}
				break;
			case PRIVATE_FAIRY_BATTLE:
				if (Info.Profile == 2) {
					Process.info.fairy.No = "2";
					return Action.PRIVATE_FAIRY_BATTLE;
				}
				if (Think.canBattle()) return Action.PRIVATE_FAIRY_BATTLE;
				break;
			case EXPLORE:
				int p = explorePoint();
				if (p > score) {
					best = Action.EXPLORE;
					score = p;
				}
				else
				{
					try {
						Thread.sleep(Random(30000, 45000)); //延时30~45秒
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					best = Action.GET_FAIRY_LIST;
					score = GFL_PRI;
				}
				break;
			case GUILD_BATTLE:
				Process.info.fairy.No = Info.PublicFairyBattle.No;
				return Action.GUILD_BATTLE;
			case GUILD_TOP:
				return Action.GUILD_TOP;
			case GET_FAIRY_REWARD:
				return Action.GET_FAIRY_REWARD;
			case PFB_GOOD:
				return Action.PFB_GOOD;
			case RECV_PFB_GOOD:
				return Action.RECV_PFB_GOOD;
			case NOTHING:
				break;
			case SELL_CARD:
				if (cardsToSell()) return Action.SELL_CARD;
				break;
			case LV_UP:
				decideUpPoint();
				return Action.LV_UP;
			case USE:
				int ptr = decideUse();
				if (ptr > score) {
					best = Action.USE;
					score = ptr;
				}
				break;
			default:
				break;
			}
		}
		return best;
	}
	
	private static int decideUse() {
		
		if (Info.autoUseAp) {
			if (Process.info.ap < Info.autoApLow) {
				switch (Info.autoApType) {
				case ALL:
					if (Process.info.halfApToday > 0 && Process.info.halfAp > 0) {
						Process.info.toUse = AP_HALF;
						return USE_PRI;
					} else {
						if (Process.info.fullAp > Info.autoApFullLow) {
							Process.info.toUse = AP_FULL;
							return USE_PRI;
						}
					}
					break;
				case FULL_ONLY:
					if (Process.info.fullAp > Info.autoApFullLow) {
						Process.info.toUse = AP_FULL;
						return USE_PRI;
					}
					break;
				case HALF_ONLY:
					if (Process.info.halfApToday > 0 && Process.info.halfAp > 0) {
						Process.info.toUse = AP_HALF;
						return USE_PRI;
					}
					break;
				default:
					break;
				
				}
			}
		}
		if (Info.autoUseBc) {
			if (Process.info.bc < Info.autoBcLow) {
				switch (Info.autoBcType) {
				case ALL:
					if (Process.info.halfBcToday > 0 && Process.info.halfBc > 0) {
						Process.info.toUse = BC_HALF;
						return USE_PRI;
					} else {
						if (Process.info.fullBc > Info.autoBcFullLow) {
							Process.info.toUse = BC_FULL;
							return USE_PRI;
						}
					}
					break;
				case FULL_ONLY:
					if (Process.info.fullBc > Info.autoBcFullLow) {
						Process.info.toUse = BC_FULL;
						return USE_PRI;
					}
					break;
				case HALF_ONLY:
					if (Process.info.halfBcToday > 0 && Process.info.halfBc > 0) {
						Process.info.toUse = BC_HALF;
						return USE_PRI;
					}
					break;
				default:
					break;
				
				}
			}
		}
		return Integer.MIN_VALUE;
	}

	private static boolean canBattle() {
		switch (Process.info.fairy.Type) {
		case 0:
			Process.info.fairy.No = Info.PublicFairyBattle.No;
			break;
		case 4:
			if (Process.info.bc >= Info.LowerBCDeck.BC) {
				if (Process.info.bc < Info.FriendFairyBattleNormal.BC) //如果小于则用舔妖卡组打
				{
					Process.info.fairy.No = Info.LowerBCDeck.No;
				}
				else
				{
					Process.info.fairy.No = Info.FriendFairyBattleNormal.No;
				}
			}
			else
				return false;
			break;
		case 5:
			if (Process.info.bc >= Info.LowerBCDeck.BC) {
				if (Info.RareFairyUseNormalDeck || Process.info.bc < Info.FriendFairyBattleRare.BC)
				{
					Process.info.fairy.No = Info.LowerBCDeck.No;
				}
				else
				{
					Process.info.fairy.No = Info.FriendFairyBattleRare.No;
				}
			}
			else
				return false;
			break;
		case 6:
			if (Process.info.bc >= Info.LowerBCDeck.BC) {
				if (Process.info.bc < Info.PrivateFairyBattleNormal.BC) //如果小于则用舔妖卡组打
				{
					Process.info.fairy.No = Info.LowerBCDeck.No;
				}
				else
				{
					Process.info.fairy.No = Info.PrivateFairyBattleNormal.No;
				}
			}
			else
				return false;
			break;
		case 7:
			if (Process.info.bc >= Info.LowerBCDeck.BC) {
				if (Info.RareFairyUseNormalDeck || Process.info.bc < Info.PrivateFairyBattleRare.BC)
				{
					Process.info.fairy.No = Info.LowerBCDeck.No;
				}
				else
				{
					Process.info.fairy.No = Info.PrivateFairyBattleRare.No;
				}
			}
			else
				return false;
			break;
		default:
			return false;
		}
		return true;
	}
	
	private static void decideUpPoint() {
		if (Info.Profile == 1) {
			//主号全加BC
			Process.info.apUp = 0;
			Process.info.bcUp = Process.info.pointToAdd;
		} else if (Info.Profile == 2) {
			//小号全加AP
			Process.info.apUp = Process.info.pointToAdd;
			Process.info.bcUp = 0;
		}
	}
	
	private static int explorePoint() {
		try {
			if (Info.Profile == 2) {
				if (Process.info.ap < 1) return Integer.MIN_VALUE;
				Process.info.front = Process.info.floor.get(1);
				return EXPLORE_URGENT;
			}
			if (Process.info.bc == 0) return Integer.MIN_VALUE;
			// 首先确定楼层
			if (Process.info.AllClear) {
				int ap = Process.info.ap / Process.info.bc * Info.PrivateFairyBattleNormal.BC;
				if (ap > 1) {
					Process.info.front = Process.info.floor.get(ap);
				} else {
					Process.info.front = Process.info.floor.get(1);
				}
			}
			if (Info.OneAPOnly) Process.info.front = Process.info.floor.get(1);
			// 判断是否可以行动
			if (Process.info.front == null) Process.info.front = Process.info.floor.get(1);
			if (Info.AllowBCInsuffient && Process.info.ap >= Process.info.front.cost) return EXPLORE_URGENT; //强制跑图
			if (Process.info.ap < Process.info.front.cost) return Integer.MIN_VALUE;
			if (Process.info.ap == Process.info.apMax) return EXPLORE_URGENT;
		} catch (Exception ex) {
			ex.printStackTrace();
			return Integer.MIN_VALUE;
		}
		return EXPLORE_NORMAL;
	}
	private static boolean cardsToSell() {
		if (Info.Profile == 2) {
			int count = 0;
			String toSell = "";
			for (Card c : Process.info.cardList) {
				if (!Info.KeepCard.contains(c.serialId)) {
					if (toSell.isEmpty()) {
						toSell = c.serialId;
					} else {
						toSell += "," + c.serialId;
					}
					count++;
				}
				if (count >= 30) break;
			}
			Process.info.toSell = toSell;
			return false; // 测试状态
			//return !toSell.isEmpty();
		} else if (Info.Profile == 1) {
			int count = 0;
			String toSell = "";
			for (Card c : Process.info.cardList) {
				if (!c.exist) continue;
				if (c.holo && c.hp >= 3500) continue; //闪卡不卖，但是低等级的闪卡照样要卖
				if (c.hp > 6000) continue; //防止不小心把贵重卡片卖了 
				if (Info.CanBeSold.contains(c.cardId)) {
					if (toSell.isEmpty()) {
						toSell = c.serialId;
					} else {
						toSell += "," + c.serialId;
					}
					count++;
					c.exist = false;
				}
				if (count >= 30) break;
			}
			
			Process.info.toSell = toSell;
			return !toSell.isEmpty();
		}
		return false;
		
	}
	/**
	 * 生成指定范围内的随机数
	 * @param Min 最小值
	 * @param Max 最大值
	 * @return long类型的随机数
	 */
	public static long Random(long Min, long Max) {
		return Math.round(Math.random() * (Max - Min) + Min);
	}
}
