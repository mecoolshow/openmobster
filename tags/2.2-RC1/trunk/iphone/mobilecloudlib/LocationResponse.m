//
//  LocationResponse.m
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "LocationResponse.h"


@implementation LocationResponse

-(id)init
{
    if(self == [super init])
    {
        attrMgr = [[GenericAttributeManager alloc] initWithRetention];
    }
    return self;
}

+(id) withInit
{
    LocationResponse *instance = [[[LocationResponse alloc] init] autorelease];
    return instance;
}

-(void) dealloc
{
    [attrMgr release];
    
    [super dealloc];
}

-(void) setAttribute:(NSString *)name :(NSString *)value
{
	[attrMgr setAttribute:name :value];
}

-(NSString *) getAttribute:(NSString *)name
{
	id value = [attrMgr getAttribute:name];
    if([value isKindOfClass:[NSString class]])
    {
        return (NSString *)value;
    }
    else
    {
        return nil;
    }
}

-(void) setListAttribute:(NSString *)name :(NSArray *)list
{
	[attrMgr setAttribute:name :list];
}

-(NSArray *) getListAttribute:(NSString *)name
{
	return (NSArray *)[attrMgr getAttribute:name];
}

-(void) setMapAttribute:(NSString *)name :(NSDictionary *)map
{
    [attrMgr setAttribute:name :map];
}

-(NSDictionary *)getMapAttribute:(NSString *)name
{
    return (NSDictionary *)[attrMgr getAttribute:name];
}


-(NSArray *)getNames
{
    return [attrMgr getNames];
}


-(void) removeAttribute:(NSString *) name
{
    [attrMgr removeAttribute:name];
}

-(id) get:(NSString *) name
{
    return [attrMgr getAttribute:name];
}
@end
