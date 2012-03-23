/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "MobileObject.h"
#import "SystemException.h"
#import "StringUtil.h"
#import "MobileObjectDatabase.h"
#import "BeanList.h"
#import "BeanListEntry.h"
#import "MobileBeanMetaData.h"
#import "SyncService.h"
#import "ErrorHandler.h"


/*!
 MobileBean is a managed Mobile Component which is an extension of its corresponding Mobile Component in the Cloud.
 
 MobileBean provides seamless access to the synchronized data. This data is used in a  Mobile application in various contexts like showing reports, GUI for the service etc
 
 It is designed to shield the Mobile Developer from low-level services like Offline Access, Receiving Notifications related to data changes on the server,
 synchronizing modified beans back with the server, etc. This helps the developer to focus on the business logic for their applications without having to
 worry about low level synchronization details
 
 @author openmobster@gmail.com
 */
@interface MobileBean : NSObject 
{
	@private
	MobileObject *data;
	BOOL isDirty;
	BOOL isNew;
	BOOL readonly;
}

@property (retain) MobileObject *data;
@property (assign) BOOL isDirty;
@property (assign) BOOL isNew;
@property (assign) BOOL readonly;


+(id)withInit:(MobileObject *)data;

/**
 * Create a new transient instance of a Mobile Bean. The Mobile Bean has to be explicitly saved in order
 * to persist it on the device and have it reflect on the server. In this case, when the bean is persisted
 * the Id will be generated by the device since its not explicitly specified
 * 
 * @param channel channel of the bean
 * @return an instance of the MobileBean originated from the device (does not have a Cloud counterpart yet, until synchronized)
 */
+(MobileBean *)newInstance:(NSString *)channel;

/**
 * Provides an instance of a Mobile Bean
 * 
 * @param channel channel of the bean
 * @param id id of the bean
 * @return an instance of the MobileBean from the channel
 */
+(MobileBean *)readById:(NSString *)channel :(NSString *)oid;

/**
 * Provides all the instances of Mobile Beans for the specified channel
 * 
 * @param the channel to read the beans from
 * @return all the beans stored in the channel
 */
+(NSArray *)readAll:(NSString *)channel;

/**
 * Checks if the Channel has been booted up on the device with initial data or not
 * 
 * @return true if the channel has been booted up
 */
+(BOOL)isBooted:(NSString *)channel;

/**
 * Returns the Channel associated with this bean
 * 
 * @return the name of the channel
 */
-(NSString *)getChannel;

/**
 * Gets the unique identifier of the bean
 * 
 * @return the unique identifier of this Mobile Bean
 */
-(NSString *)getId;

/**
 * Gets the unique identifier of this bean on the Cloud Side. 99% of the times this is same as the one on the Device Side.
 * 
 * @return the unique identifier for this bean on the cloud side
 */
-(NSString *)getCloudId;

/**
 * Checks if the bean instance is properly initialized
 * 
 * @return true if it is initialized
 */
-(BOOL)isInitialized;

/**
 * Checks if this bean was originally created on the device
 * 
 * @return true if the bean is originated on the device
 */
-(BOOL)isCreatedOnDevice;

/**
 * Checks if the particular instance is in proxy state or its state is fully downloaded from the server
 * 
 * @return true if it is not fully loaded from the Cloud
 */
-(BOOL)isProxy;

/**
 * Gets the Value of a Field of the bean
 * 
 * @param fieldUri expression identifying the field on the bean
 * @return the Value value of the field on the bean
 */
-(NSString *)getValue:(NSString *)fieldUri;

/**
 * Sets the Value of a Field of the bean
 * 
 * @param fieldUri expression identifying the field on the bean
 * @param value value to be set
 */
-(void)setValue:(NSString *)fieldUri :(NSString *)value;

/**
 * Gets the Binary Value of a Field of the bean
 * 
 * @param fieldUri expression identifying the field on the bean
 * @return the Value value of the field on the bean
 */
-(NSData *)getBinaryValue:(NSString *)fieldUri;

/**
 * Sets the Binary Value of a Field of the bean
 * 
 * @param fieldUri expression identifying the field on the bean
 * @param value value to be set
 */
-(void)setBinaryValue:(NSString *)fieldUri :(NSData *)value;

/**
 * Reads a List of Beans under the Mobile Bean "parent" Object
 * 
 * @param listProperty expression to specify the List
 * @return the listProperty
 */
-(BeanList *)readList:(NSString *)listProperty;

/**
 * Saves the List of Beans under the Mobile Bean "parent" Object
 * If the list is null, a new list is created. If the list exists, then this list replaces the old list
 * 
 * @param list expression of the list property of the bean
 */
-(void)saveList:(BeanList *)list;

/**
 * Clears the List of Beans under the Mobile Bean "parent" Object
 * 
 * @param listProperty expression of the list property to be cleared
 */
-(void)clearList:(NSString *)listProperty;

/**
 * Add a Bean to the List of Beans under the Mobile Bean "parent" Object
 * 
 * @param expression of the list property in question
 * @param bean a BeanListEntry instance to be added to the list property
 */
-(void)addBean:(NSString *)listProperty :(BeanListEntry *)bean;

/**
 * Remove the Bean present at the specified index from the List of Beans under the Mobile Bean "parent" Object
 * 
 * @param listProperty expression representing the list property
 * @param elementAt index of the element to be removed from the list property
 */
-(void)removeBean:(NSString *)listProperty :(int) elementAt;

/**
 * Query the Channel such that the criteria provided is separated by AND in the WHERE clause.
 * The criteria consists of name/value pairs of the data to be matched
 * 
 * @param channel Channel being queried
 * @param criteria name/value pairs of the data to be matched
 * @return an array of beans that match the specified query
 */
+(NSArray *) queryByEqualsAll:(NSString *) channel :(GenericAttributeManager *) criteria;

/**
 * Query the Channel such that the criteria provided is separated by an OR in the WHERE clause.
 * The criteria consists of name/value pairs of the data to be matched
 * 
 * @param channel Channel being queried
 * @param criteria name/value pairs of the data to be matched
 * @return an array of beans that match the specified query
 */
+(NSArray *) queryByEqualsAtleastOne:(NSString *) channel :(GenericAttributeManager *) criteria;

/**
 * Query the Channel such that the criteria provided is separated by a AND in the WHERE clause.
 * The criteria consists of name/value pairs of the data to be matched. In this case, it returns rows that
 * "do not" match the specified criteria
 * 
 * @param channel Channel being queried
 * @param criteria name/value pairs of the data to be matched
 * @return an array of beans that "do not" match the specified query
 */
+(NSArray *) queryByNotEqualsAll:(NSString *) channel :(GenericAttributeManager *) criteria;

/**
 * Query the Channel such that the criteria provided is separated by a OR in the WHERE clause.
 * The criteria consists of name/value pairs of the data to be matched. In this case, it returns rows that
 * "do not" match the specified criteria
 * 
 * @param channel Channel being queried
 * @param criteria name/value pairs of the data to be matched
 * @return an array of beans that "do not" match the specified query
 */
+(NSArray *) queryByNotEqualsAtleastOne:(NSString *) channel :(GenericAttributeManager *) criteria;

/**
 * Query the Channel such that the criteria provided is separated by an AND in the WHERE clause.
 * The criteria consists of name/value pairs of the data to be matched. The matching is done using the
 * LIKE clause to check if the specified criteria is contained within the data
 * 
 * @param channel Channel being queried
 * @param criteria name/value pairs of the data to be matched
 * @return an array of beans that match the specified query
 */
+(NSArray *) queryByContainsAll:(NSString *) channel :(GenericAttributeManager *) criteria;

/**
 * Query the Channel such that the criteria provided is separated by an OR in the WHERE clause.
 * The criteria consists of name/value pairs of the data to be matched. The matching is done using the
 * LIKE clause to check if the specified criteria is contained within the data
 * 
 * @param channel Channel being queried
 * @param criteria name/value pairs of the data to be matched
 * @return an array of beans that match the specified query
 */
+(NSArray *) queryByContainsAtleastOne:(NSString *) channel :(GenericAttributeManager *) criteria;

/**
 * Persists the state of the Mobile Bean. This also makes sure the consistent bean state is reflected on the Cloud Side as well
 * 
 */
-(void)save;

/**
 * Deletes the bean from the channel. This also makes sure this action is reflected on the Cloud Side as well
 */
-(void)delete;

/**
 * Re-Read the state of the MobileBean from the database
 *
 */
-(void)refresh;

//internal use only
/**
 * This method is for internal use only
 */
+(NSArray *)filterProxies:(NSArray *)mobileObjects;

/**
 * This method is for internal use only
 */
-(void)clearAll;

/**
 * This method is for internal use only
 */
-(void)clearMetaData;
@end
