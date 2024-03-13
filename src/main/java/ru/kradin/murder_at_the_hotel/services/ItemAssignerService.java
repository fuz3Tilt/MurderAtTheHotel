package ru.kradin.murder_at_the_hotel.services;

import org.springframework.stereotype.Service;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.items.*;
import ru.kradin.murder_at_the_hotel.game.roles.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ItemAssignerService {
    private List<Item> items;

    public ItemAssignerService() {
        items = new ArrayList<>();
        items.add(new ArmatureItem());
        items.add(new BlackDollarItem());
        items.add(new ConfessionItem());
        items.add(new DynamiteItem());
        items.add(new InspectionItem());
        items.add(new IodineItem());
        items.add(new PopulationСensusItem());
        items.add(new ShotgunItem());
        items.add(new SoapItem());
        items.add(new ThompsonGunItem());
    }

    public void assignItems(List<Gamer> gamers) {
        for (Gamer gamer: gamers) {
            assignRandomItems(gamer);
        }
    }

    private void assignRandomItems(Gamer gamer) {
        Bag bag = new Bag();
        List<Item> bagItems = bag.getItems();

        Class<? extends Role> gamerRoleClass = gamer.getRole().getClass();

        for (Item item: items) {
            List<Class<? extends Role>> relatedRoles = item.getRelatedRoles();
            if (relatedRoles.contains(gamerRoleClass)) {
                bagItems.add(item.clone());
                break;
            }
        }

        for (int i = 0;i<=1;i++) {
            Random random = new Random();
            int itemIndex = random.nextInt(items.size());
            bagItems.add(items.get(itemIndex).clone());
        }

        gamer.setBag(bag);
    }

}
