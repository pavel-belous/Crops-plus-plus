package com.github.bartimaeusnek.cropspp.GTHandler.machines;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Utility;
import gregtech.common.items.behaviors.Behaviour_DataOrb;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static gregtech.api.enums.Textures.BlockIcons.*;

public class CropGeneExtractor extends GT_MetaTileEntity_BasicMachine {

    public CropGeneExtractor(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 1,
                new String[]{
                        "It can extract CropGenes",
                        "Use a circuit to determine the genes you want to extract,",
                        "1 for Specimen, 2 for Growth, 3 for Gain, 4 for Resistance",
                        "Takes in 1A",
                        "Needs crop's (tier+2)/2 as Voltage level, round down (Tier 5 crop needs 7/2=~3=HV)",
                        "Can process crops up to tier " + getMaxCropTier(aTier)},
                2, 1, "Crop_Gen_Extractor.png", "",
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_SIDE_SCANNER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_SIDE_SCANNER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_SIDE_SCANNER),
                        TextureFactory.builder().addIcon(OVERLAY_SIDE_SCANNER_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_FRONT_SCANNER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_SCANNER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_FRONT_SCANNER),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_SCANNER_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_TOP_SCANNER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_TOP_SCANNER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_TOP_SCANNER),
                        TextureFactory.builder().addIcon(OVERLAY_TOP_SCANNER_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_BOTTOM_SCANNER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_BOTTOM_SCANNER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_BOTTOM_SCANNER),
                        TextureFactory.builder().addIcon(OVERLAY_BOTTOM_SCANNER_GLOW).glow().build()));
    }

    public CropGeneExtractor(String mName, byte mTier, String[] mDescriptionArray, ITexture[][][] mTextures, String mGUIName, String mNEIName) {
        super(mName, mTier, 1, mDescriptionArray, mTextures, 2, 1, mGUIName, mNEIName);
        // TODO Auto-generated constructor stub
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new CropGeneExtractor(this.mName, this.mTier, this.mDescriptionArray, this.mTextures, this.mGUIName, this.mNEIName);
    }

    public static int getMaxCropTier(int mTier){
        return (mTier * 2 - 1);
    }

    @Override
    public int checkRecipe(boolean skipOC) {
        ItemStack aStack = getInputAt(0);
        ItemStack bStack = getInputAt(1);
        ItemStack tosave = getSpecialSlot();

        if (ItemList.IC2_Crop_Seeds.isStackEqual(bStack, true, true) && ItemList.Circuit_Integrated.isStackEqual(aStack, true, true)) {
            ItemStack helper = bStack;
            bStack = aStack;
            aStack = helper;
        }


        if (ItemList.IC2_Crop_Seeds.isStackEqual(aStack, true, true) && ItemList.Circuit_Integrated.isStackEqual(bStack, true, true) && ItemList.Tool_DataOrb.isStackEqual(tosave, false, true)) {
            NBTTagCompound tNBT = aStack.getTagCompound();
            if (tNBT == null || tNBT.getString("name").isEmpty())
                return DID_NOT_FIND_RECIPE;
            if (getOutputAt(0) != null)
                return DID_NOT_FIND_RECIPE;
            CropCard card = Crops.instance.getCropCard(tNBT.getString("owner"), tNBT.getString("name"));
            if(card.tier() > getMaxCropTier(this.mTier))
                return DID_NOT_FIND_RECIPE;
            if(bStack.getItemDamage() < 1 || bStack.getItemDamage() > 4)
                return DID_NOT_FIND_RECIPE;
            byte[] GrGaRe = new byte[3];
            GrGaRe[0] = tNBT.getByte("growth");
            GrGaRe[1] = tNBT.getByte("gain");
            GrGaRe[2] = tNBT.getByte("resistance");
            this.mOutputItems[0] = ItemList.Tool_DataOrb.get(1L, new Object[0]);

            aStack.stackSize -= 1;
            tosave.stackSize -= 1;

            switch (bStack.getItemDamage()) {
                case 1:
                    Behaviour_DataOrb.setDataTitle(this.mOutputItems[0], "Crop-Specimen-Scan");
                    Behaviour_DataOrb.setDataName(this.mOutputItems[0], tNBT.getString("owner") + ":" + tNBT.getString("name"));
                    break;
                case 2:
                    Behaviour_DataOrb.setDataTitle(this.mOutputItems[0], "Crop-Growth-Scan");
                    Behaviour_DataOrb.setDataName(this.mOutputItems[0], Byte.toString(GrGaRe[0]));
                    break;
                case 3:
                    Behaviour_DataOrb.setDataTitle(this.mOutputItems[0], "Crop-Gain-Scan");
                    Behaviour_DataOrb.setDataName(this.mOutputItems[0], Byte.toString(GrGaRe[1]));
                    break;
                case 4:
                    Behaviour_DataOrb.setDataTitle(this.mOutputItems[0], "Crop-Resistance-Scan");
                    Behaviour_DataOrb.setDataName(this.mOutputItems[0], Byte.toString(GrGaRe[2]));
                    break;
                default:
                    break;
            }
            long power = GT_Values.V[(card.tier() + 2) / 2];
            calculateOverclockedNess((int) (power - (power / 10)), 6000);
            if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1)
                return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
            return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
        }

        return DID_NOT_FIND_RECIPE;

    }

    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 1) {
            GT_Utility.doSoundAtClient((String) GregTech_API.sSoundList.get(212), 10, 1.0F, aX, aY, aZ);
        }
    }

    public void startProcess() {
        sendLoopStart((byte) 1);
    }

    @Override
    public boolean canInsertItem(int aIndex, ItemStack aStack, int aSide) {
        if (ItemList.Circuit_Integrated.isStackEqual(aStack, true, true) || ItemList.IC2_Crop_Seeds.isStackEqual(aStack, true, true))
            return isValidSlot(aIndex) && aStack != null && aIndex < mInventory.length && (mInventory[aIndex] == null || GT_Utility.areStacksEqual(aStack, mInventory[aIndex])) && allowPutStack(getBaseMetaTileEntity(), aIndex, (byte) aSide, aStack);
        return false;
    }
}
