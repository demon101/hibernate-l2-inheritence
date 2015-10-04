package com.test.hibernate;

import com.vladmihalcea.hibernate.masterclass.laboratory.util.AbstractTest;
import org.hibernate.Session;
import org.junit.Test;

import javax.persistence.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by demon on 04.10.15.
 */
public class L2InheritanceTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[] {
                EntityBase.class,
                EntityA.class,
                EntityB.class,
                HolderEntity.class,
        };
    }

    @Test
    public void testSequenceIdentifierGenerator() {
        LOGGER.debug("testSequenceIdentifierGenerator");
        doInTransaction(new TransactionCallable<Void>() {
            @Override
            public Void execute(Session session) {
                EntityA entityA = new EntityA();
                session.persist(entityA);
                HolderEntity holderEntity = new HolderEntity();
                holderEntity.setHoldedEntity(entityA);
                session.persist(holderEntity);
                session.flush();
                return null;
            }
        });

        doInTransaction(new TransactionCallable<Void>() {
            @Override
            public Void execute(Session session) {
                HolderEntity o = (HolderEntity) session.get(HolderEntity.class, 1);// loads from L2 cache
                assertEquals("Wrong holded entity class", EntityA.class, o.getHoldedEntity().getClass());
//                LOGGER.debug(o.toString());
                return null;
            }
        });
    }



}

@Entity(name="some_table")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name="TYPE", discriminatorType=DiscriminatorType.STRING )
abstract class EntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column
    private int id;
}

@Entity
@DiscriminatorValue("EntityA")
@Cacheable
class EntityA extends EntityBase {
    @Column
    private int aColumn;

    public int getAColumn() {
        return aColumn;
    }

    public void setAColumn(int aColumn) {
        this.aColumn = aColumn;
    }
}

@Entity
@DiscriminatorValue("EntityB")
@Cacheable
class EntityB extends EntityBase {
    @Column
    private int bColumn;

    public int getBColumn() {
        return bColumn;
    }

    public void setBColumn(int bColumn) {
        this.bColumn = bColumn;
    }
}

@Entity(name="holder_table")
@Cacheable
class HolderEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column
    private int id;

    @ManyToOne(fetch=FetchType.LAZY)
    EntityBase holdedEntity;

    public EntityBase getHoldedEntity() {
        return holdedEntity;
    }

    public void setHoldedEntity(EntityBase holdedEntity) {
        this.holdedEntity = holdedEntity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "HolderEntity{" +
                "id=" + id +
                ", holdedEntity=" + holdedEntity +
                '}';
    }
}
