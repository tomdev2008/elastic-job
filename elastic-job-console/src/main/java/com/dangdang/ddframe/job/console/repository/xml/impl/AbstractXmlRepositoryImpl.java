/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.console.repository.xml.impl;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.dangdang.ddframe.job.console.exception.JobConsoleException;
import com.dangdang.ddframe.job.console.repository.xml.XmlRepository;
import com.dangdang.ddframe.job.console.util.HomeFolder;

public abstract class AbstractXmlRepositoryImpl<E> implements XmlRepository<E> {
    
    private final File file;
    
    private final Class<E> clazz;
    
    private JAXBContext jaxbContext;
    
    protected AbstractXmlRepositoryImpl(final String fileName, final Class<E> clazz) {
        file = new File(HomeFolder.getFilePathInHomeFolder(fileName));
        this.clazz = clazz;
    }
    
    @PostConstruct
    private void init() {
        HomeFolder.createHomeFolderIfNotExisted();
        try {
            jaxbContext = JAXBContext.newInstance(clazz);
        } catch (final JAXBException ex) {
            throw new JobConsoleException(ex);
        }
    }
    
    @Override
    public synchronized E load() {
        if (!file.exists()) {
            try {
                return clazz.newInstance();
            } catch (final InstantiationException | IllegalAccessException ex) {
                throw new JobConsoleException(ex);
            }
        }
        try {
            @SuppressWarnings("unchecked")
            E result = (E) jaxbContext.createUnmarshaller().unmarshal(file);
            return result;
        } catch (final JAXBException ex) {
            throw new JobConsoleException(ex);
        }
    }
    
    @Override
    public synchronized void save(final E entity) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(entity, file);
        } catch (final JAXBException ex) {
            throw new JobConsoleException(ex);
        }
    }
}
